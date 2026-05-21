# 테스트 작성 규칙

## 대상 및 범위

| 레이어 | 테스트 여부 | 도구 |
| ------ | ----- | ---- |
| Service | 필수 | JUnit 5 + Mockito |
| Controller | 필수 | MockMvc (`@WebMvcTest`) |
| Repository | 제외 | SQL은 통합 테스트 별도 판단 |

---

## 파일 위치

테스트 파일은 대상 클래스와 동일한 패키지 경로에 `Test` 접미사로 생성한다.

```
src/main/java/com/picketlogia/picket/api/product/service/ProductService.java
src/test/java/com/picketlogia/picket/api/product/service/ProductServiceTest.java
```

---

## 클래스 구조

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;
}
```

- `@ExtendWith(MockitoExtension.class)` 사용
- 클래스 `@DisplayName`은 테스트 대상 클래스명으로 작성

---

## 메서드 네이밍

메서드명은 **테스트 대상 상황을 즉시 파악할 수 있는 영어**로 작성한다.
`@DisplayName`에 한국어로 테스트 의도를 작성한다.

```java
// ✅ 올바른 예
@Test
@DisplayName("존재하지 않는 공연 ID로 조회하면 예외가 발생한다")
void findById_throwsException_whenProductNotFound() { ... }

@Test
@DisplayName("유효한 요청으로 공연을 등록하면 저장 후 응답을 반환한다")
void create_returnsResponse_whenValidCommand() { ... }

@Test
@DisplayName("이미 취소된 예매를 취소하면 예외가 발생한다")
void cancel_throwsException_whenAlreadyCancelled() { ... }

// 잘못된 예
void test1() { ... }
void 공연등록테스트() { ... }
void createProduct() { ... }  // 상황 정보 없음
```

메서드명 패턴: `{대상메서드}_{결과}_{조건}` (조건이 명확할 때만 조건 추가)

---

## Given / When / Then 구조

모든 테스트 메서드는 `// given`, `// when`, `// then` 주석으로 블록을 구분한다.
첫 줄은 빈 줄로 시작한다 (`@rules/coding-convention.md` 준수).

```java
@Test
@DisplayName("유효한 요청으로 공연을 등록하면 저장 후 응답을 반환한다")
void create_returnsResponse_whenValidCommand() {

    // given
    ProductCreateCommand command = ProductCreateCommand.builder()
        .sellerId(1L)
        .title("레미제라블")
        .genreId(1L)
        .price(80000)
        .build();

    Seller seller = new Seller();
    seller.setId(1L);

    given(sellerRepository.findById(1L)).willReturn(Optional.of(seller));

    // when
    ProductResponse response = productService.create(command);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getTitle()).isEqualTo("레미제라블");
    then(productRepository).should().save(any(Product.class));
}
```

---

## 예외 검증

예외 발생 케이스는 `assertThatThrownBy`로 검증한다.

```java
@Test
@DisplayName("존재하지 않는 판매자 ID로 공연을 등록하면 예외가 발생한다")
void create_throwsException_whenSellerNotFound() {

    // given
    ProductCreateCommand command = ProductCreateCommand.builder()
        .sellerId(999L)
        .title("레미제라블")
        .build();

    given(sellerRepository.findById(999L)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> productService.create(command))
        .isInstanceOf(BaseException.class)
        .hasMessageContaining(BaseResponseStatus.SELLER_NOT_FOUND.getMessage());
}
```

---

## Mockito 스타일

BDD 스타일을 사용한다.

```java
// BDD 스타일
given(productRepository.findById(id)).willReturn(Optional.of(product));
then(productRepository).should().save(any());
then(productRepository).should(never()).delete(any());

// classic 스타일 (사용 금지)
when(productRepository.findById(id)).thenReturn(Optional.of(product));
verify(productRepository).save(any());
```

---

## Assertion 스타일

`AssertJ`를 사용한다. JUnit의 `assertEquals` 직접 사용 금지.

```java
// AssertJ
assertThat(response.getTitle()).isEqualTo("레미제라블");
assertThat(response.getPrice()).isGreaterThan(0);
assertThat(list).hasSize(3).extracting("title").contains("레미제라블");

// JUnit assertions (사용 금지)
assertEquals("레미제라블", response.getTitle());
```

---

## 공통 규칙 요약

| 항목 | 규칙 |
| ---- | ---- |
| 테스트 프레임워크 | JUnit 5 + Mockito + AssertJ |
| 클래스 어노테이션 | `@ExtendWith(MockitoExtension.class)` |
| 메서드명 | 영어, 상황을 즉시 파악 가능한 서술형 |
| DisplayName | 한국어, 테스트 의도 명확히 서술 |
| 구조 | given / when / then 주석 필수 |
| Mock 스타일 | BDD (`given`, `then`) |
| Assertion | AssertJ (`assertThat`) |
| 예외 검증 | `assertThatThrownBy` |
