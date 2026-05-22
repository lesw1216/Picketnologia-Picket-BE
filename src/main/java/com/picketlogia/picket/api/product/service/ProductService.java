package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.genre.dto.result.GenreResult;
import com.picketlogia.picket.api.genre.service.GenreService;
import com.picketlogia.picket.api.product.dto.request.ProductSearchRequest;
import com.picketlogia.picket.api.product.dto.result.ProductDetailResult;
import com.picketlogia.picket.api.product.dto.result.ProductsResult;
import com.picketlogia.picket.api.product.dto.result.SalesProductResult;
import com.picketlogia.picket.api.product.dto.result.UpcomingProductResult;
import com.picketlogia.picket.api.product.dto.request.ProductQueryRequest;
import com.picketlogia.picket.api.product.dto.request.ProductRegisterRequest;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.repository.ProductQueryRepository;
import com.picketlogia.picket.api.product.repository.ProductRepository;
import com.picketlogia.picket.api.product.validator.BaseProductValidator;
import com.picketlogia.picket.api.seat.dto.command.SeatGradeSaveCommand;
import com.picketlogia.picket.api.seat.dto.command.SeatSaveCommand;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.service.SeatGradeService;
import com.picketlogia.picket.api.seat.service.SeatService;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Integer PAGE_SIZE = 10;
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final GenreService genreService;
    private final PerformanceRoundService performanceRoundService;
    private final SeatGradeService seatGradeService;
    private final SeatService seatService;
    private final ProductQueryRepository productQueryRepository;
    private final List<BaseProductValidator> productValidators;

    /**
     * 공연 상품 등록 전 과정(검증·DB 저장·회차/좌석 등록·이미지 업로드)을 수행한다.
     *
     * @param userIdx               등록 요청자 ID
     * @param productRegisterRequest 상품 등록 요청 정보
     * @param files                  업로드할 이미지 파일 목록
     * @return 저장 결과를 반영한 요청 객체
     * @throws BaseException 등록 과정 중 오류가 발생했을 때 GLOBAL_EXCEPTION 으로 래핑
     */
    public ProductRegisterRequest register(Long userIdx,
                                           ProductRegisterRequest productRegisterRequest,
                                           List<MultipartFile> files) {

        try {
            productValidators.forEach(validator -> validator.validate(productRegisterRequest));

            Product product = saveProductWithGenre(productRegisterRequest, userIdx);
            registerRoundsAndSeats(product, productRegisterRequest);
            productImageService.upload(product, files);

            return ProductRegisterRequest.fromEntity(product);

        } catch (Exception e) {
            log.error("[ERROR] ", e);
            throw BaseException.from(BaseResponseStatus.GLOBAL_EXCEPTION);
        }
    }

    private Product saveProductWithGenre(ProductRegisterRequest productRegisterRequest, Long userIdx) {

        GenreResult findGenre = genreService.findByCode(productRegisterRequest.getGenre());

        return productRepository.save(productRegisterRequest.toEntity(findGenre.getIdx(), userIdx));
    }

    private void registerRoundsAndSeats(Product product, ProductRegisterRequest productRegisterRequest) {

        performanceRoundService.register(productRegisterRequest.getRoundOption(), product);

        Map<SeatGradeStatus, Long> seatGradeMap = seatGradeService.saveAll(
                product.getIdx(),
                SeatGradeSaveCommand.fromList(productRegisterRequest.getSeatGrade())
        );
        seatService.saveAll(
                product.getIdx(),
                SeatSaveCommand.fromSeatMap(productRegisterRequest.getSeatMap()),
                seatGradeMap
        );
    }

    /**
     * 쿼리 조건(장르·페이지·정렬)에 맞춰 상품을 페이징 조회한다.
     *
     * @param query 쿼리 조건이 담긴 요청
     * @return 페이징 결과, page 가 null 이거나 결과가 없으면 null
     */
    public ProductsResult findAllByQueryPaging(ProductQueryRequest query) {

        if (query.getPage() == null) {
            return null;
        }

        Sort sort = getSort(query.getSort());
        PageRequest pageRequest = PageRequest.of(query.getPage() - 1, PAGE_SIZE, sort);

        Page<Product> findProducts = productRepository.findByGenre_Code(query.getGenre(), pageRequest);

        if (findProducts == null) {
            return null;
        }

        return ProductsResult.from(
                findProducts.getContent(), findProducts.getNumber() + 1, findProducts.getTotalPages()
        );
    }

    private Sort getSort(String sort) {

        if (sort == null) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }

        return switch (sort) {
            case "REVIEW_RATING" -> Sort.by(Sort.Order.desc("reviewRating"));
            case "REVIEW_COUNT" -> Sort.by(Sort.Order.desc("reviewCount"));
            default -> Sort.by(Sort.Order.desc("createdAt"));
        };
    }

    /**
     * 상품 ID로 상세 정보를 조회한다.
     *
     * @param productIdx 상품 ID
     * @return 상세 결과
     * @throws BaseException 상품이 존재하지 않을 때
     */
    public ProductDetailResult findProductDetailById(Long productIdx) {

        Product product = productRepository.findByIdx(productIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.PRODUCT_NOT_FOUND));

        return ProductDetailResult.from(product);
    }

    /**
     * 검색어와 정렬 조건으로 상품을 조회한다.
     *
     * @param productSearchRequest 검색 조건 (이름·장르)
     * @param sort                 정렬 조건
     * @return 검색 결과 목록
     */
    public List<SalesProductResult> searchAndSort(ProductSearchRequest productSearchRequest, String sort) {

        List<Product> products = productQueryRepository.searchAndSort(productSearchRequest, sort);

        return products.stream().map(SalesProductResult::from).toList();
    }

    /**
     * 장르 코드에 해당하는 상품을 최신순으로 10개씩 페이징해 첫 페이지를 반환한다.
     *
     * @param code 장르 코드
     * @return 페이징 결과
     */
    public ProductsResult findAllByGenre(String code) {

        Page<Product> products = productRepository.findByGenre_CodeOrderByCreatedAtDesc(code, PageRequest.of(0, PAGE_SIZE));

        return ProductsResult.from(
                products.getContent(),
                products.getNumber(),
                products.getTotalPages()
        );
    }

    /**
     * 장르 코드 기준으로 오픈 예정 상위 5개 상품을 조회한다.
     *
     * @param genreCode 장르 코드
     * @return 오픈 예정 상품 목록
     */
    public List<UpcomingProductResult> findUpcomingProductsByGenreCode(String genreCode) {

        List<Product> products = productRepository.findTop5ByGenre_CodeOrderByOpenDateDescPage(
                genreCode,
                LocalDateTime.now(),
                PageRequest.of(0, 5)
        );

        return products.stream().map(UpcomingProductResult::from).toList();
    }

    /**
     * 장르 구분 없이 오픈 예정일이 빠른 상위 5개 상품을 조회한다.
     *
     * @return 오픈 예정 상품 목록
     */
    public List<UpcomingProductResult> findUpcomingProducts() {

        List<Product> products = productRepository.findTop5ByOpenDateAfterOrderByOpenDateAsc(LocalDateTime.now());

        return products.stream().map(UpcomingProductResult::from).toList();
    }

    /**
     * 판매량이 많은 순으로 장르별 상위 5개 상품을 조회한다.
     *
     * @param genre 장르 코드
     * @return 판매 랭킹 상품 목록
     */
    public List<SalesProductResult> findTop5ByGenreOrderBySalesCount(String genre) {

        List<Product> products = productRepository.findTop5ByGenre_CodeOrderBySalesCountDesc(genre);

        return products.stream().map(SalesProductResult::from).toList();
    }
}
