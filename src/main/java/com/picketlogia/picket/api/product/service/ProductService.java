package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.genre.model.GenreRead;
import com.picketlogia.picket.api.genre.service.GenreService;
import com.picketlogia.picket.api.product.model.*;
import com.picketlogia.picket.api.product.model.dto.ProductQuery;
import com.picketlogia.picket.api.product.model.dto.register.ProductRegister;
import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.product.repository.ProductQueryRepository;
import com.picketlogia.picket.api.product.repository.ProductRepository;
import com.picketlogia.picket.api.product.service.validator.BaseProductValidator;
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
import java.util.Optional;

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

    // 상품 등록
    public ProductRegister register(Long userIdx, ProductRegister dto, List<MultipartFile> files) {

        try {
            // 상품 등록에 필요한 Validator 실행
            productValidators.forEach(validator -> validator.validate(dto));

            GenreRead findGenre = genreService.findByCode(dto.getGenre());

            // 상품 DB 저장
            Product product = productRepository.save(
                    dto.toEntity(findGenre.getIdx(), userIdx)
            );

            // 회차 등록
            performanceRoundService.register(dto.getRoundOption(), product);

            // 좌석 정보 등록
            Map<SeatGradeStatus, Long> seatGradeMap = seatGradeService.saveAll(
                    product.getIdx(),
                    SeatGradeSaveCommand.fromList(dto.getSeatGrade())
            );
            seatService.saveAll(product.getIdx(), SeatSaveCommand.fromSeatMap(dto.getSeatMap()), seatGradeMap);

            // 이미지 업로드
            productImageService.upload(product, files);

            return ProductRegister.fromEntity(product);
        } catch (Exception e) {
            log.error("[ERROR] ", e);
            throw BaseException.from(BaseResponseStatus.GLOBAL_EXCEPTION);
        }

    }

    /**
     * 쿼리를 사용한 상품 목록 조회
     *
     * @param query 쿼리 <code>DTO</code>
     * @return <code>ProductListByPage</code>
     */
    public ProductListByPage findAllByQueryPaging(ProductQuery query) {

        if (query.getPage() != null) {
            Sort sort = getSort(query.getSort());
            PageRequest pageRequest = PageRequest.of(query.getPage() - 1, PAGE_SIZE, sort);

            Page<Product> findProducts = productRepository.findByGenre_Code(query.getGenre(), pageRequest);

            if (findProducts != null) {
                return ProductListByPage.from(
                        findProducts.getContent(), findProducts.getNumber()+1, findProducts.getTotalPages()
                );
            }
        }

        return null;
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
     * 상품을 상세 조회 한다.
     *
     * @param idx 상품의 IDX
     * @return 상품 상세 <code>DTO</code>
     */
    public ProductReadForDetail findProductDetailById(Long idx) {
        // 상품 상세 조회
        Optional<Product> product = productRepository.findByIdx(idx);

        if (product.isPresent()) {
            Product entity = product.get();

            return ProductReadForDetail.from(entity);
        }

        return null;
    }

    //상품  검색 및 정렬
    public List<ProductReadForList> searchAndSort(ProductSearchDto dto, String sort) {
        List<Product> result = productQueryRepository.searchAndSort(dto, sort);

        return result.stream().map(ProductReadForList::from).toList();
    }

    /**
     * 장르별 상품 조회, 페이지는 기본 첫번째 페이지, 최신순 10개의 데이터를 가지고 온다.
     *
     * @param code 장르 code
     * @return <code>ProductListByPage</code>
     */
    public ProductListByPage findAllByGenre(String code) {

        Page<Product> findProducts = productRepository.findByGenre_CodeOrderByCreatedAtDesc(
                code,
                PageRequest.of(0, PAGE_SIZE)
        );

        return ProductListByPage.from(
                findProducts.getContent(),
                findProducts.getNumber(),
                findProducts.getTotalPages()
        );
    }

    /**
     * 장르별로 오픈 예정일이 제일 빠른 5개의 상품을 조회
     *
     * @param code 장르 식별자
     * @return <code>List<<code>ProductUpcomingRead</code>></code>
     */
    public List<ProductReadForUpcoming> findUpcomingProductsByGenreCode(String code) {

//        List<Product> findProducts = productRepository.findTop5ByGenre_CodeAndOpenDateAfterOrderByOpenDateAsc(
//                code,
//                LocalDateTime.now()
//        );

        List<Product> findProducts = productRepository.findTop5ByGenre_CodeOrderByOpenDateDescPage(
                code,
                LocalDateTime.now(),
                PageRequest.of(0, 5)
        );

        return findProducts.stream().map(ProductReadForUpcoming::from).toList();
    }

    /**
     * 장르별로 오픈 예정일이 제일 빠른 5개의 상품을 조회
     *
     * @return <code>List<<code>ProductUpcomingRead</code>></code>
     */
    public List<ProductReadForUpcoming> findUpcomingProducts() {

        List<Product> findProducts = productRepository.findTop5ByOpenDateAfterOrderByOpenDateAsc(LocalDateTime.now());
        return findProducts.stream().map(ProductReadForUpcoming::from).toList();

    }

    /**
     * 판매량이 많은 공연을 기준으로 장르별로 5개의 상품을 조회
     *
     * @param genre 장르
     * @return List<< code>ProductReadForList</code>>
     */
    public List<ProductReadForList> findTop5ByGenreOrderBySalesCount(String genre) {

        List<Product> findProducts = productRepository.findTop5ByGenre_CodeOrderBySalesCountDesc(genre);
        return findProducts.stream().map(ProductReadForList::from).toList();

    }
}
