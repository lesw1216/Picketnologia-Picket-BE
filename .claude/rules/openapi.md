# OpenAPI 명세 규칙

엔드포인트 추가·변경 시 `docs/api/openapi.yaml` 을 업데이트한다.
OpenAPI 3.0 스펙을 따른다.

---

## 파일 위치

```
docs/api/openapi.yaml   ← 전체 API 명세를 하나의 파일로 관리
```

---

## 기본 구조

```yaml
openapi: 3.0.3
info:
  title: Picket API
  version: 1.0.0
servers:
  - url: http://localhost:8080
paths:
  ...
components:
  schemas:
    ...
```

---

## paths 작성 규칙

```yaml
paths:
  /products:
    get:
      summary: 공연 상품 목록 조회
      tags:
        - Product
      responses:
        '200':
          description: 조회 성공
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductListResponse'
    post:
      summary: 공연 상품 등록
      tags:
        - Product
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProductCreateRequest'
      responses:
        '200':
          description: 등록 성공
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductResponse'
        '400':
          description: 입력값 오류
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
```

- `tags` 는 도메인 단위로 묶는다 (Product, Reservation, Seat, User, Seller)
- 경로 파라미터는 `{id}` 형식으로 표기

---

## components/schemas 작성 규칙

스키마 이름은 DTO 클래스명과 동일하게 사용한다.

```yaml
components:
  schemas:
    ProductCreateRequest:
      type: object
      required:
        - title
        - genreId
        - regionId
        - price
      properties:
        title:
          type: string
        genreId:
          type: integer
          format: int64
        regionId:
          type: integer
          format: int64
        price:
          type: integer
          minimum: 0
        startDate:
          type: string
          format: date
        endDate:
          type: string
          format: date

    ProductResponse:
      type: object
      properties:
        isSuccess:
          type: boolean
        code:
          type: integer
        message:
          type: string
        result:
          $ref: '#/components/schemas/ProductResult'

    ErrorResponse:
      type: object
      properties:
        isSuccess:
          type: boolean
          example: false
        code:
          type: integer
        message:
          type: string
```

- 모든 응답은 `BaseResponse<T>` 래퍼를 반영한다 (`isSuccess`, `code`, `message`, `result`)
- Enum 값은 실제 열거형 값과 동일하게 작성한다
- `ErrorResponse` 는 공통 스키마로 한 번만 정의하고 `$ref` 로 재사용한다

---

## 공통 규칙

| 항목 | 규칙 |
|------|------|
| 포맷 | YAML |
| 버전 | OpenAPI 3.0.3 |
| 파일 | `docs/api/openapi.yaml` 단일 파일 |
| 태그 | 도메인 단위 (Product, Reservation, Seat, User, Seller) |
| 스키마명 | DTO 클래스명과 동일 |
| 응답 구조 | 항상 `BaseResponse<T>` 래퍼 반영 |
| 에러 응답 | 400, 404 등 예상 가능한 오류는 반드시 명시 |
