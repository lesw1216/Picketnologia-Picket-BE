# Token/Logout/Session Policy Notes

## 배경

- 로그인 성공 시 `Access Token`, `Refresh Token` 발급은 완료됨
- 현재 Redis 저장 구조는 `RefreshToken -> AccessToken`
- 고민 포인트는 로그아웃 시 Redis 데이터를 어떻게 처리할지, 그리고 `Refresh Token` 만료 기간을 왜 길게 가져가는지에 대한 정책 판단

## 1. Redis 구조를 어떻게 가져갈 것인가

### 현재 구조

- Redis: `RefreshToken -> AccessToken`
- 의미:
  - `RefreshToken`을 기준으로 `AccessToken`을 찾아 재발급 검증에 사용

### 고민한 대안 1

- 로그아웃 시 `AccessToken` 쿠키만 삭제
- Redis에 남아 있는 `RefreshToken -> AccessToken`은 만료 시점까지 그대로 둠

### 고민한 대안 2

- Redis 구조를 `AccessToken -> RefreshToken`으로 변경
- 로그아웃 시 `AccessToken`을 key로 하는 값을 삭제
- 이후 모든 요청마다 Redis에서 `AccessToken` 존재 여부를 조회해서 로그아웃 여부를 판단
- 사실상 블랙리스트처럼 사용

## 2. 어떤 방식이 더 적절한가

### `AccessToken -> RefreshToken` + 매 요청 Redis 조회 방식

- 권장하지 않음
- 이유:
  - 모든 요청마다 Redis 조회가 필요해짐
  - JWT의 무상태성 장점이 크게 줄어듦
  - 트래픽이 늘수록 비용이 커짐
  - 사실상 "매 요청 블랙리스트 조회" 구조가 됨

### `RefreshToken -> AccessToken` 구조 유지

- 현재 구조 자체는 괜찮음
- 다만 로그아웃 시 `AccessToken` 쿠키만 삭제하고 `RefreshToken`을 Redis에 남겨두는 것은 비추천
- 이유:
  - 로그아웃 이후에도 `RefreshToken`이 살아 있으면 재발급 가능성이 남음
  - 서버 입장에서 "로그아웃 완료"가 명확하지 않음

## 3. 로그아웃 시 무엇을 삭제해야 하는가

### 핵심 판단

- 로그아웃에서 더 중요한 것은 `AccessToken`이 아니라 `RefreshToken`을 무효화하는 것

### 이유

- `AccessToken`은 원래 짧은 수명을 가짐
- 장기 세션 유지 및 재발급 권한은 `RefreshToken`이 가지고 있음
- 따라서 로그아웃 시에는 다음이 필요함

1. 브라우저의 `AccessToken` 쿠키 삭제
2. 브라우저의 `RefreshToken` 쿠키 삭제
3. Redis에서 해당 `RefreshToken` 삭제

### 현실적인 운영 방식

- `AccessToken`은 블랙리스트로 관리하지 않음
- 대신 만료 시간을 짧게 유지
- `RefreshToken`만 서버 저장소에서 철회 가능하게 관리

### 결론

- 추천 방식:
  - Redis 구조는 `RefreshToken -> AccessToken` 유지 가능
  - 로그아웃 시 `RefreshToken`을 Redis에서 삭제
  - `AccessToken`은 쿠키에서 제거
- 비추천 방식:
  - `AccessToken -> RefreshToken`로 바꾸고 매 요청마다 Redis 조회

## 4. 그런데 Refresh Token은 왜 길게 유지하는가

### 이유

- 사용자 편의 때문
- `AccessToken`은 짧게 두고, `RefreshToken`은 길게 둬서 사용자가 자주 다시 로그인하지 않도록 함

### 역할 차이

- `AccessToken`
  - 짧은 수명
  - 탈취 시 피해를 짧게 제한
  - 실제 API 인증에 사용

- `RefreshToken`
  - 긴 수명
  - 만료된 `AccessToken`을 다시 발급받기 위한 용도
  - 로그인 유지 경험을 제공

### 만약 Refresh Token도 짧다면

- `AccessToken` 만료 후 매번 다시 로그인해야 할 수 있음
- 자동 로그인/로그인 유지의 의미가 거의 사라짐

## 5. Refresh Token 만료 기간은 반드시 7일이어야 하는가

아님. 서비스 정책에 따라 다름.

### 하루 정도로 둬도 되는가

- 가능함
- 특히 보안 민감 서비스라면 더 짧게 가져가는 것도 합리적

### 7일 같은 긴 기간을 쓰는 경우

- 브라우저를 닫았다가 다시 열어도 로그인 유지 경험을 제공하려는 경우
- 사용자가 재로그인 없이 며칠간 서비스를 다시 사용할 수 있게 하려는 정책

### 정답은 정책에 따라 달라짐

- `1일`, `7일`, `14일`, `30일` 모두 가능
- 핵심은 "우리 서비스가 로그인 유지 경험을 어디까지 제공할 것인가"에 달려 있음

## 6. 창을 닫았을 때 로그인 유지가 되는 이유

### 쿠키 정책에 따라 달라짐

- 세션 쿠키:
  - 브라우저 종료 시 사라짐
- 영속 쿠키:
  - `Max-Age` 또는 `Expires`가 있으면 브라우저를 닫아도 남아 있음

### 현재 구조 관점

- `AccessToken`은 세션 쿠키로 둘 수 있음
- `RefreshToken`은 `Max-Age`가 있는 영속 쿠키로 둘 수 있음

그러면:

1. 브라우저를 닫으면 `AccessToken` 쿠키는 사라질 수 있음
2. `RefreshToken` 쿠키는 남아 있을 수 있음
3. 사용자가 다시 접속하면 `RefreshToken`으로 `AccessToken`을 재발급
4. 결과적으로 로그인 유지처럼 동작

즉, "창을 닫아도 로그인 유지"는 꼭 `AccessToken`이 살아 있어서가 아니라, `RefreshToken`이 남아 있어서 가능한 것

## 7. 서비스 정책별 구조 정리

### 정책 A. 브라우저 종료 시 로그인 종료

- `AccessToken`: 세션 쿠키
- `RefreshToken`: 세션 쿠키 또는 매우 짧은 영속 쿠키
- 브라우저를 닫으면 로그인 종료
- 장기 `RefreshToken`의 필요성이 낮음

### 정책 B. 브라우저를 닫아도 며칠간 로그인 유지

- `AccessToken`: 짧은 수명
- `RefreshToken`: 영속 쿠키 + 상대적으로 긴 만료 기간
- 재접속 시 `RefreshToken`으로 `AccessToken` 재발급
- 일반적인 "로그인 유지" UX

## 최종 정리

- `RefreshToken`을 길게 두는 이유는 로그인 유지 UX 때문
- 하지만 길게 두는 만큼 로그아웃 시 서버에서 확실히 철회해야 함
- 따라서 현재 구조에서는:

1. Redis 구조는 `RefreshToken -> AccessToken` 유지 가능
2. 로그아웃 시 `RefreshToken` 쿠키 삭제
3. Redis에서 해당 `RefreshToken` 삭제
4. `AccessToken`은 쿠키에서 제거
5. `AccessToken` 블랙리스트를 위해 매 요청 Redis 조회는 하지 않는 방향이 더 적절

