# SessionId And Used Refresh Notes

## 1. `sessionId`를 왜 도입하는가

기존 `RefreshToken -> AccessToken` 구조만으로는 다음 문제가 있다.

- old RT가 이미 회전되어 삭제된 뒤 다시 들어왔을 때
- 그 RT가 어떤 세션 소속이었는지 알기 어렵다
- 현재 살아 있는 최신 RT/AT를 찾아 무효화하기 어렵다

이를 해결하기 위해 세션 단위 식별자인 `sessionId`를 도입한다.

중요한 점:

- `sessionId`는 사용자별 고유값이 아님
- 로그인 세션별 고유값임

즉, 한 사용자가 로그인할 때마다 새로운 `sessionId`가 생길 수 있다.

## 2. `sessionId`는 무엇으로 생성하는가

현재 단계에서는 `UUID`로 생성하는 것이 가장 현실적이다.

추천 방식:

```java
UUID.randomUUID().toString()
```

이 값을 세션 식별자 용도로 사용한다.

역할 분리:

- `RefreshToken` = 재발급용 비밀값
- `sessionId` = 세션 추적용 식별자

둘은 역할이 다르므로 분리하는 것이 맞다.

## 3. 권장 Redis 구조

현재 방향에서는 다음 구조가 적절하다.

1. `refresh:{rt} -> sessionId`
2. `session:{sessionId} -> { currentRefreshToken, currentAccessToken, userId, status }`
3. `used_refresh:{oldRt} -> sessionId`

### `refresh:{rt} -> sessionId`

용도:

- 정상 재발급 시 요청 RT로 세션을 바로 찾기 위한 인덱스

### `session:{sessionId} -> ...`

용도:

- 현재 세션의 최신 토큰쌍 저장
- 요청한 RT/AT가 현재 세션의 최신 토큰쌍인지 검증
- 탈취 의심 시 세션 전체 무효화

### `used_refresh:{oldRt} -> sessionId`

용도:

- 이미 회전되어 삭제된 old RT가 다시 들어왔을 때
- 그 RT가 어느 세션 소속이었는지 찾기 위한 보조 키
- RT reuse detection에 사용

## 4. `used_refresh:{oldRt}` TTL은 어떻게 잡는가

추천:

- `AccessToken` 만료 시간 정도

예:

- `AccessToken` TTL이 15분이면
- `used_refresh:{oldRt}`도 15분 정도 유지

이유:

- old RT 재사용 감지는 보통 회전 직후 짧은 시간 구간이 핵심
- 너무 짧으면 감지를 놓칠 수 있음
- 너무 길면 Redis에 흔적이 과하게 쌓임

즉, `AccessToken` TTL 정도가 가장 무난한 기준이다.

## 5. 왜 `used_refresh` TTL이 짧아도 되는가

이유는 다음과 같다.

- `AccessToken`은 15분 정도의 짧은 수명을 가짐
- 그 이후 세션은 다시 새 토큰쌍으로 갱신될 가능성이 높음
- old RT 재사용 감지도 보통 회전 직후가 중요함

즉, old RT를 장기간 추적할 실익은 크지 않다.

## 6. 재발급 로직은 어떻게 바뀌는가

### 정상 재발급

1. 요청 RT로 `refresh:{rt}` 조회
2. `sessionId` 획득
3. `session:{sessionId}` 조회
4. 요청 AT와 저장된 현재 AT 비교
5. 요청 RT와 저장된 현재 RT 비교
6. 둘 다 맞으면 새 AT/RT 발급
7. `refresh:{oldRt}` 삭제
8. `used_refresh:{oldRt} -> sessionId` 저장
9. `refresh:{newRt} -> sessionId` 저장
10. `session:{sessionId}`의 현재 RT/AT 갱신
11. 쿠키 전송

### old RT 재사용 감지

1. 요청 RT로 `refresh:{rt}` 조회
2. 없으면 바로 실패하지 않음
3. `used_refresh:{rt}` 조회
4. 있으면 이 RT는 이미 회전된 old RT로 판단
5. 해당 `sessionId` 세션을 무효화
6. 로그아웃 처리

### 둘 다 없는 경우

- 그냥 만료되었거나 존재하지 않는 RT로 보고 일반 실패 처리

## 7. 이 구조가 의미하는 것

즉 로직은 다음처럼 바뀐다.

- `refresh:{rt}`가 있으면 정상 재발급 흐름
- `refresh:{rt}`가 없으면 `used_refresh:{rt}` 확인
- `used_refresh:{rt}`가 있으면 RT 재사용 감지
- 해당 세션 무효화

## 8. RT를 JWT로 바꿔야 하는가

현재 판단으로는 지금 당장은 바꾸지 않는 쪽이 낫다.

이유:

- 이미 Redis 기반 상태 저장 구조를 사용 중임
- RT를 JWT로 바꾸지 않아도 `sessionId` 기반 추적 가능
- 공수가 더 적음
- 설계 복잡도가 덜 증가함

즉 지금은:

- RT는 UUID 유지
- `sessionId`는 Redis에서 관리
- `used_refresh`로 old RT 재사용 감지

이 구조가 가장 현실적이다.

## 9. RT를 JWT로 바꾸면 어떤 장점이 있는가

장점:

- old RT만 보고도 `sessionId`를 추출 가능
- Redis에서 이미 삭제된 RT도 토큰 자체로 세션 계열 추적 가능

하지만 단점도 있다.

- 서명/검증 로직 추가
- 클레임 설계 필요
- 만료/파싱 처리 복잡도 증가
- 구현 공수 증가

즉 이론적으로는 장점이 있지만, 지금 단계에서는 과하다.

## 10. 현재 추천 방향

지금 추천하는 방향은 다음과 같다.

1. `RefreshToken`은 계속 UUID 기반으로 유지
2. 로그인 시 `sessionId`도 UUID로 생성
3. Redis에 다음 키 구조 도입
   - `refresh:{rt} -> sessionId`
   - `session:{sessionId} -> current RT/AT`
   - `used_refresh:{oldRt} -> sessionId`
4. old RT 재사용 감지 시 세션 단위 무효화

## 요약

- `sessionId`는 사용자별 고유값이 아니라 로그인 세션별 고유값이다
- `sessionId`는 UUID로 생성하면 충분하다
- RT는 지금은 JWT로 바꾸지 않는 것이 더 현실적이다
- `used_refresh:{oldRt}`는 old RT 재사용 감지를 위해 필요하다
- `used_refresh` TTL은 `AccessToken` 만료 시간 정도가 적절하다
- 전체 구조는 `refresh`, `session`, `used_refresh` 3개 축으로 가져가는 것이 맞다

