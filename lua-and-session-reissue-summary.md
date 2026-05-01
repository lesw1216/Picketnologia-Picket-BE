# Lua And Session Reissue Summary

## 1. Lua script로 재발급 로직을 처리하는 이유

토큰 재발급은 단순 조회가 아니라 다음 과정을 포함한다.

1. 기존 `RefreshToken`으로 Redis 조회
2. 저장된 `AccessToken`과 요청 `AccessToken` 비교
3. 기존 `RefreshToken` 삭제
4. 새 `RefreshToken -> AccessToken` 저장

이 작업이 여러 Redis 명령으로 나뉘면 동시 요청에서 경쟁 조건이 생길 수 있다.

예:

- 같은 `RefreshToken`으로 동시에 재발급 요청 2개가 들어옴
- 둘 다 조회 성공
- 둘 다 비교 성공
- 둘 다 새 토큰 발급
- 결국 하나의 RT로 두 번 재발급됨

이를 막기 위해 Lua script로 조회, 비교, 삭제, 저장을 Redis 내부에서 한 번에 실행한다.

## 2. Lua script의 원자성 의미

Lua script는 Redis 서버 내부에서 하나의 작업처럼 실행된다.

- 실행 중간에 다른 요청이 끼어들 수 없음
- 조회 + 비교 + 삭제 + 저장을 한 덩어리로 처리 가능

즉, 재발급 과정에서 다음 위험을 막는다.

1. 같은 RT로 중복 재발급되는 문제
2. old RT와 new RT가 동시에 잠깐 살아남는 문제
3. RT rotation이 무력화되는 문제
4. 동시 요청으로 세션 상태가 꼬이는 문제

## 3. `@Transactional`로는 왜 부족한가

`@Transactional`은 여기서 원하는 수준의 안전성을 보장하지 못한다.

이유:

- Redis 조회, 비교, 삭제, 저장이 애플리케이션 레벨에서 분리됨
- compare-and-swap 성격의 조건부 갱신을 보장하지 못함
- 동시 재발급 경쟁 조건을 막지 못함

즉, 이 문제는 일반적인 RDB 트랜잭션보다 Redis 내부 원자 처리 문제에 가깝다.

## 4. `WATCH`보다 Lua가 더 적합한 이유

`WATCH`도 가능은 하지만 재발급 로직엔 Lua가 더 적합하다.

이유:

1. 네트워크 왕복이 더 적음
2. 재시도 로직이 덜 필요함
3. 조회 + 비교 + 삭제 + 저장 같은 조건부 로직을 더 자연스럽게 표현 가능
4. 코드가 단순함

정리:

- `WATCH`는 충돌 감지 + 재시도 모델
- Lua는 판정 + 상태 변경을 Redis 안에서 한 번에 끝내는 모델

## 5. `redisTemplate.execute()`와 Lua 인자 매핑

현재 구조에서:

```java
redisTemplate.execute(
    reissueTokensScript,
    List.of(previousRefreshToken, reissuedRefreshToken.getValue()),
    requestAccessToken,
    reissuedAccessToken,
    String.valueOf(refreshTokenExpireMillis)
);
```

매핑은 다음과 같다.

- `List.of(...)`
  - Lua의 `KEYS`
  - `KEYS[1] = previousRefreshToken`
  - `KEYS[2] = reissuedRefreshToken.getValue()`

- 그 뒤의 값들
  - Lua의 `ARGV`
  - `ARGV[1] = requestAccessToken`
  - `ARGV[2] = reissuedAccessToken`
  - `ARGV[3] = refreshTokenExpireMillis`

## 6. Lua 결과값 처리

Lua script는 결과 코드로 다음을 반환하는 구조가 적절하다.

- `1` : 정상 재발급
- `0` : 기존 `RefreshToken` 없음
- `-1` : `AccessToken` 불일치

또한 `RedisScript`의 `resultType`을 `Long.class`로 명시했고 Lua도 항상 숫자를 반환하면,
`result == null ? 0L : result` 같은 방어 코드는 사실상 불필요하다.

## 7. Lua의 원자성과 DB 롤백의 차이

Lua는 원자적으로 실행되지만, 일반적인 RDB 트랜잭션 롤백과 완전히 같은 개념은 아니다.

정확히는:

- 스크립트 실행 중간에 다른 요청이 끼어들지 못함
- 전체가 한 작업처럼 실행됨
- 하지만 RDB의 일반적인 rollback 개념과 동일하다고 이해하면 안 됨

현재처럼 단순한 재발급 로직에서는 충분히 적절하고 안전한 방식이다.

## 8. 해커가 먼저 RT/AT를 탈취해서 재발급하는 경우

가능한 시나리오는 다음과 같다.

1. 정상 사용자: `RT1`, `AT1` 보유
2. 해커가 `RT1`, `AT1` 탈취
3. 해커가 먼저 재발급 요청
4. 서버가 `RT2`, `AT2` 발급
5. Redis에는 최신 토큰쌍이 저장됨
6. 정상 사용자가 뒤늦게 `RT1`로 재발급 시도
7. `RT1`은 이미 폐기되었으므로 정상 사용자는 실패
8. 해커는 여전히 최신 토큰쌍을 보유

즉, 원자성은 경쟁 조건은 막지만, 탈취 후 먼저 재발급한 해커를 자동으로 이기게 해주진 않는다.

## 9. 현재 `RefreshToken -> AccessToken` 구조의 한계

현재 구조는 다음은 잘 처리한다.

- 동시 재발급 경쟁 조건
- RT rotation의 원자성

하지만 다음은 충분히 처리하지 못한다.

1. old RT 재사용 시 그 RT가 누구의 어떤 세션 것이었는지 추적
2. 현재 살아 있는 최신 RT/AT를 찾아 세션 전체를 끊는 일
3. “요청한 AT가 해당 RT 세션에서 발급된 AT인지” 엄격히 확인하는 일

즉, 단순 `RefreshToken -> AccessToken` 구조만으로는 탈취 재사용 대응이 부족하다.

## 10. 세션 기반 구조가 필요한 이유

핵심은 토큰 문자열 자체보다 “로그인 세션 계열”을 추적하는 것이다.

이를 위해 필요한 정보:

- `userId`
- `sessionId`
- 현재 유효한 `RefreshToken`
- 현재 유효한 `AccessToken`

여기서 `sessionId`는 사용자별 고유값이 아니라 로그인 세션별 고유값이다.

예:

1. 사용자 로그인
2. `sessionId = S1` 생성
3. `RT1`, `AT1` 발급
4. 재발급이 일어나도 새 `RT2`, `AT2`는 여전히 세션 `S1` 소속

즉 토큰 문자열은 바뀌어도 같은 세션이면 같은 `sessionId`를 공유한다.

## 11. 권장 Redis 키 구조

### 기본 구조

1. `refresh:{refreshToken}` -> `sessionId`
2. `session:{sessionId}` -> `{ userId, currentRefreshToken, currentAccessToken, status }`

### 선택 구조

3. `user_sessions:{userId}` -> `sessionId` 집합

용도:

- 사용자 전체 세션 강제 종료
- 다중 기기 세션 관리

## 12. 왜 `refresh:{rt}` 인덱스와 `session:{sessionId}` 본체가 둘 다 필요한가

`refresh:{rt}` 인덱스는 정상 재발급 흐름에서 RT로 바로 세션을 찾기 쉽게 해준다.

`session:{sessionId}` 본체는 현재 세션의 최신 RT/AT를 저장해서 다음을 가능하게 한다.

- 요청한 RT가 현재 세션의 최신 RT인지 검증
- 요청한 AT가 현재 세션의 최신 AT인지 검증
- 탈취 의심 시 현재 세션 전체 무효화

즉:

- RT 인덱스 = 빠른 조회용
- 세션 본체 = 현재 상태 저장용

## 13. RT 안에 `sessionId`를 넣는 이유

old RT가 이미 Redis에서 삭제된 뒤 재사용될 수도 있다.

이때도 그 old RT가 어떤 세션 소속이었는지 알아야 현재 최신 세션을 끊을 수 있다.

그래서 RT 자체에도 `sessionId`를 넣는 것이 유리하다.

즉 old RT만 있어도:

1. RT를 파싱해서 `sessionId` 추출
2. `session:{sessionId}` 조회
3. 현재 최신 RT/AT 무효화

가 가능해진다.

## 14. 정상 재발급 흐름

### 로그인 시

1. `sessionId` 생성
2. `AccessToken`, `RefreshToken` 발급
3. `RefreshToken` 안에 `sessionId` 포함
4. `refresh:{rt} -> sessionId` 저장
5. `session:{sessionId} -> { currentAccessToken, currentRefreshToken }` 저장
6. 쿠키로 두 토큰 전송

### 재발급 정상 처리

1. 요청 RT로 `sessionId` 찾기
2. `session:{sessionId}` 조회
3. 요청 AT와 현재 저장된 AT 비교
4. 요청 RT와 현재 저장된 RT 비교
5. 둘 다 맞으면 새 AT/RT 발급
6. `refresh:{oldRt}` 삭제
7. `refresh:{newRt} -> sessionId` 저장
8. `session:{sessionId}`의 현재 AT/RT 갱신
9. 쿠키 전송

이 작업도 Lua로 원자 처리하는 방향이 맞다.

## 15. old RT 재사용 감지 흐름

예:

1. 해커가 먼저 `RT1`, `AT1`로 재발급
2. 세션 `S1`의 현재 토큰쌍이 `RT2`, `AT2`로 갱신
3. 정상 사용자가 뒤늦게 `RT1`로 재발급 시도
4. `refresh:{RT1}` 조회는 실패
5. 하지만 `RT1` 안에 포함된 `sessionId = S1`은 추출 가능
6. `session:{S1}` 조회
7. 현재 `currentRefreshToken != RT1` 이면 RT 재사용 감지
8. 세션 `S1` 전체 무효화
9. 로그인 다시 요구

핵심은:

- 해커가 새로 받은 RT 문자열을 직접 알 필요는 없음
- `sessionId`만 알면 현재 세션의 최신 토큰쌍을 찾아 무효화 가능

## 16. 사용자가 브라우저를 닫고 오래 접속하지 않은 경우

만약 해커가 `AT + RT` 둘 다 탈취했다면,
정상 사용자가 브라우저를 닫고 오래 접속하지 않아도 해커가 그 사이 재발급을 계속 시도할 수 있다.

즉 브라우저를 닫았다는 사실 자체가 방어가 되진 않는다.

핵심 전제는:

1. 해커가 무엇을 탈취했는가
2. 재발급 시 서버가 무엇을 요구하는가

정리:

- `AT + RT` 둘 다 탈취: 해커가 계속 사용할 가능성 있음
- `RT`만 탈취: 재발급 정책에 따라 제한 가능

## 17. AT, RT 모두 탈취되었을 때의 사전 대응

완전 방지는 어렵고 피해를 줄이는 방향으로 가야 한다.

필요한 대응:

1. `AccessToken` 수명 짧게 유지
2. `RefreshToken` rotation
3. RT reuse detection
4. `sessionId` 기반 세션 추적
5. `HttpOnly`, `Secure`, `SameSite=Lax` 등 쿠키 보안 적용
6. XSS 방어
7. 이상 징후 감지
8. 민감 작업 재인증

## 18. IP 변경만으로 탈취로 단정 가능한가

단정하면 안 된다.

이유:

- 모바일 네트워크 변경
- 와이파이 전환
- NAT
- VPN
- 회사망

등으로 정상 사용자도 IP가 자주 바뀔 수 있다.

따라서 IP 변경은 “즉시 차단 신호”가 아니라 “위험 신호”로만 쓰는 것이 맞다.

## 19. 쿠키 보안을 잘 적용했을 때의 의미

`HttpOnly`, `Secure`, `SameSite=Lax`를 적용하고 XSS, CSRF도 잘 막는다면
일반적인 웹 공격 경로로 쿠키가 털릴 가능성은 많이 줄어든다.

하지만 확률을 숫자로 말할 수는 없다.

여전히 남는 위험:

- 사용자 기기 악성코드
- 브라우저 확장
- 서버 측 취약점
- 운영 실수
- 공급망 공격

즉 확률이 0이 되는 것은 아니므로, RT rotation과 reuse detection 같은 후속 방어가 필요하다.

## 20. 지금 구현 우선순위

### 지금 바로 필요한 것

1. 짧은 `AccessToken`
2. `RefreshToken` rotation
3. Lua 기반 원자 재발급
4. 로그아웃 시 RT 폐기
5. 로그인/재발급 실패 처리

### 바로 다음 단계

6. `sessionId` 기반 Redis 구조 도입
7. RT reuse detection

### 지금 당장 안 해도 되는 것

- IP 기반 즉시 차단
- 고급 이상 탐지 엔진
- 복잡한 fingerprint 결합
- 전 사용자 세션 강제 종료 체계

## 요약

- Lua는 재발급 경쟁 조건을 막기 위해 필요하다
- 하지만 탈취 후 먼저 재발급한 해커를 자동으로 제어하진 못한다
- 그 문제를 해결하려면 `sessionId` 기반 세션 추적 구조가 필요하다
- 권장 구조는 `refresh:{rt} -> sessionId` 와 `session:{sessionId} -> 현재 RT/AT` 이다
- 더 안전하게 하려면 RT 안에도 `sessionId`를 넣어야 한다
- 최종 목표는 RT 재사용 감지 시 세션 전체를 무효화하는 구조다

