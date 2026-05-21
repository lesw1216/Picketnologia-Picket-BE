# 코딩 컨벤션

## API 응답

모든 REST 응답은 `BaseResponse<T>` 래퍼를 사용한다.

```java
return ResponseEntity.ok(BaseResponse.success(response));
```

---

## Enum

- `UserType`: `USER`, `SELLER`
- 도메인별 상태 Enum은 해당 도메인 패키지 안에서 정의한다.

---

## JPA / QueryDSL

- DB 컬럼명은 `snake_case`, Java 필드는 `camelCase` 로 작성한다.
- 단건 조회 Repository 메서드 반환 타입은 `Optional<T>` 로 선언한다.

```java
// Repository 인터페이스
Optional<Product> findById(Long id);

// Service에서 사용
Product product = productRepository.findById(id)
    .orElseThrow(() -> new BaseException(BaseResponseStatus.PRODUCT_NOT_FOUND));
```

---

## CORS

`http://localhost:5173` 에만 허용한다.

---

## 예외 처리

`BaseException(BaseResponseStatus.XXX)` 만 사용한다. `IllegalArgumentException`, `IllegalStateException` 직접 throw 금지.
상세 규칙은 `@rules/error-handling.md` 참조.

---

## DTO

Entity를 Controller 응답 또는 Response DTO로 직접 반환하지 않는다.
DTO 레이어 분리 규칙은 `@rules/dto-layer.md`, 작성 방법은 `@rules/dto-create.md` 참조.

---

## 메서드 작성 규칙

### Javadoc

모든 `public` 메서드에 Javadoc을 작성한다. `private` 메서드는 이름만으로 의도가 충분히 전달되면 생략 가능하다.

- 첫 줄: 메서드가 하는 일을 한 문장으로 작성한다 (한국어)
- `@param`: 파라미터마다 작성한다 (타입 생략, 의미 한 줄)
- `@return`: 반환값이 있으면 작성한다 (`void` 제외)
- `@throws`: 의도적으로 던지는 예외만 작성한다

```java
/**
 * 공연 상품을 등록하고 생성된 상품 정보를 반환한다.
 *
 * @param command 상품 등록에 필요한 데이터
 * @return 생성된 상품 응답
 * @throws BaseException 판매자가 존재하지 않을 때
 */
public ProductResponse create(ProductCreateCommand command) { ... }

/**
 * 예매를 취소하고 취소된 결과를 반환한다.
 *
 * @param id      취소할 예매 ID
 * @param command 취소 요청 정보
 * @return 취소된 예매 응답
 * @throws BaseException 예매가 없거나 이미 취소된 상태일 때
 */
public ReservationResponse cancel(Long id, ReservationCancelCommand command) { ... }
```

---

### 메서드 첫 줄 공백

메서드 시그니처 바로 아래 줄은 반드시 빈 줄로 시작한다.

```java
// 올바른 예
public ProductResponse create(ProductCreateCommand command) {

    Seller seller = sellerRepository.findById(command.getSellerId())
        .orElseThrow(() -> new BaseException(BaseResponseStatus.SELLER_NOT_FOUND));

    Product product = buildProduct(command, seller);
    productRepository.save(product);

    return ProductResponse.from(ProductResult.from(product));
}

// 잘못된 예
public ProductResponse create(ProductCreateCommand command) {
    Seller seller = sellerRepository.findById(command.getSellerId())
        .orElseThrow(() -> new BaseException(BaseResponseStatus.SELLER_NOT_FOUND));
    ...
}
```

### 문맥별 공백 구분

논리적으로 다른 작업 사이에는 빈 줄을 삽입하여 가독성을 높인다.

```java
public ReservationResponse cancel(Long id) {

    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.RESERVATION_NOT_FOUND));

    validateCancellable(reservation);

    reservation.cancel();
    reservationRepository.save(reservation);

    return ReservationResponse.from(ReservationResult.from(reservation));
}
```

### 파라미터 줄바꿈

파라미터가 2개 이상이고 한 줄에 담기 어려울 때, 첫 번째 파라미터는 메서드명과 같은 줄에 두고 두 번째 파라미터부터 줄바꿈한다.
들여쓰기는 첫 번째 파라미터의 시작 열에 맞춘다.

```java
// 올바른 예 — 첫 번째는 같은 줄, 두 번째부터 줄바꿈
public List<ProductResponse> search(Long genreId,
                                    Long regionId,
                                    String keyword) {

    return productRepository.search(genreId, regionId, keyword);
}

void save(@Param("userId") Long userId,
          @Param("productId") Long productId,
          @Param("seatIdx") Integer seatIdx);

// 파라미터가 1개이거나 한 줄에 들어오면 줄바꿈 불필요
public ProductResponse findById(Long id) { ... }

// 잘못된 예 — 첫 번째 파라미터부터 줄바꿈
public List<ProductResponse> search(
        Long genreId,
        Long regionId,
        String keyword) { ... }
```

---

### 메서드 최대 10줄

메서드 바디가 10줄을 초과하면 반드시 별도 메서드로 추출한다.
단, 빈 줄과 닫는 중괄호는 줄 수에 포함하지 않는다.

```java
// 추출 후
public ProductResponse create(ProductCreateCommand command) {

    Seller seller = findSellerOrThrow(command.getSellerId());
    Product product = buildProduct(command, seller);

    productRepository.save(product);

    return toResponse(product);
}

private Seller findSellerOrThrow(Long sellerId) {

    return sellerRepository.findById(sellerId)
        .orElseThrow(() -> new BaseException(BaseResponseStatus.SELLER_NOT_FOUND));
}

private Product buildProduct(ProductCreateCommand command, Seller seller) {

    return Product.builder()
        .seller(seller)
        .title(command.getTitle())
        .genreId(command.getGenreId())
        .price(command.getPrice())
        .build();
}
```
