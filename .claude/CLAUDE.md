# PICKET

## 프로젝트 개요

Picket은 공연 예매 플랫폼 백엔드입니다. 실시간 좌석 선택 및 동시성 제어가 핵심 기능입니다.

- **Stack**: Java 17, Spring Boot 3.5.4, MariaDB, Redis, WebSocket(STOMP)
- **결제**: PortOne(포트원) SDK
- **파일 저장**: AWS S3
- **인프라**: Docker, Kubernetes, Jenkins CI/CD

## 빌드 및 실행 명령

```bash
# 빌드
./gradlew build

# 테스트 전체 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.picketlogia.picket.api.seat.service.SeatInfoServiceTest"

# 단일 테스트 메서드 실행
./gradlew test --tests "com.picketlogia.picket.api.seat.service.SeatInfoServiceTest.테스트메서드명"

# 애플리케이션 실행 (환경변수 필요)
./gradlew bootRun
```

## 필수 환경 변수

`application.yml`에서 참조하는 환경 변수:

| 변수명 | 설명 |
|--------|------|
| `DB_URL` | MariaDB 접속 URL |
| `DB_USERNAME` / `DB_PASSWORD` | DB 인증 정보 |
| `REDIS_HOST` | Redis 호스트 |
| `access_key` / `secret_key` / `bucket` | AWS S3 설정 |
| `mail_username` / `mail_password` | Gmail SMTP |
| `PORT_ONE_API_SECRET` / `STORE_ID` / `WEB_HOOK_SECRET` | PortOne 결제 설정 |
| `PWD_RESET_REDIRECT_URL` | 비밀번호 재설정 리다이렉트 URL |

## 패키지 구조

```
com.picketlogia.picket
├── api/                  # 도메인별 패키지
│   ├── auth/             # 인증 (이메일 인증, 비밀번호 찾기)
│   ├── token/            # JWT 토큰 발급·재발급
│   ├── user/             # 회원 관리
│   ├── seller/           # 판매자 기능
│   ├── product/          # 공연 상품 등록·조회
│   ├── seat/             # 좌석 조회·점유·해제
│   ├── reservation/      # 예매 관리
│   ├── payments/         # 결제·웹훅
│   ├── review/           # 리뷰
│   ├── qna/              # Q&A
│   ├── genre/            # 장르 코드
│   ├── region/           # 지역 코드
│   └── sortoption/       # 정렬 옵션
├── common/               # BaseEntity, BaseResponse, BaseResponseStatus, 공통 예외
├── config/               # Security, Redis, Swagger, 필터, 핸들러
├── websocket/            # STOMP WebSocket 설정 및 컨트롤러
└── utils/                # 직렬화 유틸
```

각 도메인 패키지 내부 구조: `controller / service / repository / model / dto(request·response·result·command)`

## 아키텍처 핵심 사항

### 인증 흐름 (JWT + Redis)
- `LoginFilter` → 로그인 성공 시 `LoginSuccessHandler`가 Access Token + Refresh Token 발급
- `AccessTokenFilter`가 모든 요청에서 Bearer 토큰 검증
- Refresh Token은 Redis에 저장, 재발급 시 Lua 스크립트로 원자적 토큰 교체 (`TokenRepository.reissueTokensAtomically`)
- `KeyExpiredListener`: Redis TTL 만료 이벤트 감지

### 실시간 좌석 동시성 제어
- 좌석 선택 시 `SeatHoldService`가 Redis Hash에 임시 선점 상태 저장 (`seat-status : {roundTimeIdx}`)
- 개별 좌석 락은 `seat:{roundTimeIdx}-{seatIdx}` 키·값 형식으로 별도 저장, TTL 10분
- WebSocket(STOMP)으로 좌석 상태 변경을 구독자에게 브로드캐스트 (`/topic/...`)
- 결제 완료 전 `validateRockSeats`로 선점 유효성 검증 후 DB 예약 처리

### 응답 형식
모든 API는 `BaseResponse<T>`로 감싸서 반환:
```java
BaseResponse.success(results)     // code: 20000
BaseResponse.error(status)        // BaseResponseStatus enum 참고
```

### 권한 구조
- `UserType.USER` / `UserType.SELLER` 두 가지 역할
- `/seller/**`, `POST /products`는 SELLER 권한 필요
- Spring Security `AccessTokenFilter`에서 JWT 파싱 후 `SecurityContext` 설정

### QueryDSL
`src/main/generated/`에 Q클래스 생성됨. 모델 변경 시 `./gradlew compileJava`로 재생성 필요.
