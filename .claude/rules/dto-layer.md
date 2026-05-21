# DTO 레이어 규칙

요청·응답 객체는 레이어별로 역할이 분리됩니다. 네이밍을 반드시 지켜야 합니다.

## 네이밍 규칙

| 접미어 | 레이어 | 설명 |
|--------|--------|------|
| `**Request` | Controller | HTTP 요청 바인딩 |
| `**Command` | Service | 데이터 변경 요청 |
| `**Query` | Service | 데이터 조회·필터 요청 |
| `**Result` | Repository | Repository 반환 객체 |
| `**Response` | Controller | API 응답 페이로드 |

## DTO 흐름

```
HTTP 요청
  └─▶ **Request       (Controller 수신)
        └─▶ **Command / **Query   (Service로 전달, Controller가 변환)
                └─▶ Repository 호출
                      └─▶ **Result       (Repository 반환, Service가 수신)
                            └─▶ **Response     (Service가 변환, Controller가 반환)
                                  └─▶ BaseResponse<**Response>  (클라이언트 전달)
```

## 계층 간 독립성 원칙

- Controller는 `**Result` 를 알아서는 안 됨
- Service는 `**Request` 를 알아서는 안 됨
- Controller ↔ Service 간 Entity 직접 전달 금지
- 각 DTO는 해당 레이어 안에서만 생성·소비

## 예시 (product 도메인)

```
productCreateRequest    - Controller가 HTTP Body로 수신
productCreateCommand    - Controller가 Request를 변환해 Service에 전달
productSearchQuery      - Controller가 쿼리 파라미터를 변환해 Service에 전달
productResult           - Repository가 조회 결과를 Service에 반환
productResponse         - Service가 Result를 변환해 Controller에 반환
```
