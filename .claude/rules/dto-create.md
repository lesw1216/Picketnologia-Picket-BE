# DTO 작성 규칙

DTO 레이어 역할 분리는 @rules/dto-layer.md 를 따른다.
이 문서는 각 DTO의 실제 작성 방법을 정의한다.

---

## Request (Controller 수신)

HTTP 요청 파라미터를 바인딩한다.
Bean Validation 어노테이션으로 입력값을 검증한다.

```java
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "공연 제목은 필수입니다.")
    private String title;

    @NotNull(message = "장르 ID는 필수입니다.")
    private Long genreId;

    @NotNull(message = "지역 ID는 필수입니다.")
    private Long regionId;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public ProductCreateCommand toCommand() {
        return ProductCreateCommand.builder()
            .title(this.title)
            .genreId(this.genreId)
            .regionId(this.regionId)
            .price(this.price)
            .startDate(this.startDate)
            .endDate(this.endDate)
            .description(this.description)
            .build();
    }
}
```

- `@Getter` + `@NoArgsConstructor` 사용
- `@Setter` 금지
- Controller에서 `@RequestBody @Valid` 로 수신
- `toCommand()` 메서드로 Command 변환 — **Request가 Command를 import하는 방향은 허용**
  (반대 방향인 `Command.from(Request)` 는 Service 레이어가 HTTP 레이어를 알게 되어 금지)
- Entity, Command 등 다른 타입을 필드로 포함하지 않는다

---

## Command (Service 데이터 변경 요청)

Controller가 Request를 변환하여 Service에 전달한다.
Service는 Request를 알아서는 안 된다.

```java
@Getter
@Builder
public class ProductCreateCommand {

    private final String title;
    private final Long genreId;
    private final Long regionId;
    private final Integer price;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;
}
```

Controller에서는 `request.toCommand()` 로 변환한다.

```java
// Controller
productService.create(request.toCommand());
```

- `@Getter` + `@Builder` 사용
- 필드는 `final` 로 선언 (불변)
- Validation 어노테이션 없음 (이미 Request에서 검증 완료)
- `Command.from(Request)` 금지 — Command가 Request를 import하면 Service 레이어에 HTTP 레이어 의존성이 생김

---

## Query (Service 데이터 조회 요청)

조회 조건을 담아 Service에 전달한다.

```java
@Getter
@Builder
public class ProductSearchQuery {

    private final Long genreId;
    private final Long regionId;
    private final String keyword;
}
```

Query는 Controller에서 직접 빌드한다.
(Request가 별도로 없는 쿼리 파라미터 기반 조회는 Controller에서 빌드하는 것이 자연스럽다.)

```java
// Controller
ProductSearchQuery query = ProductSearchQuery.builder()
    .genreId(genreId)
    .regionId(regionId)
    .keyword(keyword)
    .build();
productService.search(query);
```

- `@Getter` + `@Builder` 사용
- 필드는 `final` 로 선언 (불변)
- 단순 단건 조회(ID 기반)는 별도 Query 없이 파라미터로 전달 가능

---

## Result (Repository 반환)

Repository가 Service에 반환하는 객체.
도메인 Entity를 Service 외부로 노출하지 않기 위해 사용한다.

```java
@Getter
@Builder
public class ProductResult {

    private final Long id;
    private final String title;
    private final Long sellerId;
    private final String sellerName;
    private final String genreName;
    private final String regionName;
    private final Integer price;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime createdAt;

    public static ProductResult from(Product product) {
        return ProductResult.builder()
            .id(product.getId())
            .title(product.getTitle())
            .sellerId(product.getSeller().getId())
            .sellerName(product.getSeller().getName())
            .genreName(product.getGenre().getName())
            .regionName(product.getRegion().getName())
            .price(product.getPrice())
            .startDate(product.getStartDate())
            .endDate(product.getEndDate())
            .createdAt(product.getCreatedAt())
            .build();
    }
}
```

- `@Getter` + `@Builder` 사용
- 필드는 `final` 로 선언 (불변)
- 도메인 Entity → Result 변환 메서드 `from()` 을 Result 내부에 정의
- JOIN 조회 결과(sellerName, genreName 등 비정규화 필드)도 Result에 포함 가능

---

## Response (API 응답)

클라이언트에 반환하는 최종 응답 객체.
Service가 Result를 변환하여 생성한다.

```java
@Getter
@Builder
public class ProductResponse {

    private final Long id;
    private final String title;
    private final String sellerName;
    private final String genreName;
    private final String regionName;
    private final Integer price;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime createdAt;

    public static ProductResponse from(ProductResult result) {
        return ProductResponse.builder()
            .id(result.getId())
            .title(result.getTitle())
            .sellerName(result.getSellerName())
            .genreName(result.getGenreName())
            .regionName(result.getRegionName())
            .price(result.getPrice())
            .startDate(result.getStartDate())
            .endDate(result.getEndDate())
            .createdAt(result.getCreatedAt())
            .build();
    }
}
```

- `@Getter` + `@Builder` 사용
- 필드는 `final` 로 선언 (불변)
- Result → Response 변환 메서드 `from()` 을 Response 내부에 정의
- Enum은 `.name()` 으로 문자열 변환하여 반환
- Controller는 `BaseResponse<ProductResponse>` 로 래핑하여 반환

---

## 도메인 Entity

JPA Entity로, DTO와 규칙이 다르다.

```java
@Entity
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    // ...
}
```

- JPA Entity는 `@Entity` 사용
- Entity는 Controller 응답으로 직접 반환하지 않는다

---

## 공통 규칙

| 항목 | 규칙 |
|------|------|
| Lombok (DTO) | `@Getter` + `@Builder` 기본, `@Setter` 금지 |
| Lombok (Entity) | `@Getter` (JPA 호환, setter 대신 생성자·빌더) |
| 불변성 | Command / Query / Result / Response 필드는 `final` |
| Request → Command 변환 | `Request.toCommand()` 메서드 사용 |
| Query 변환 | Controller에서 직접 빌드 |
| Result → Response 변환 | `Response.from(Result)` 정적 메서드 |
| Entity 노출 | Response / Controller에서 Entity 직접 반환 금지 |
| Validation | Request에만 Bean Validation 어노테이션 사용 |

---

## 패키지 구조 예시

```
api/product/dto/
├── ProductCreateRequest.java
├── ProductCreateCommand.java
├── ProductSearchQuery.java
├── ProductResult.java
└── ProductResponse.java
```
