package com.picketlogia.picket.api.product.controller;

import com.picketlogia.picket.api.product.dto.result.ProductsResult;
import com.picketlogia.picket.api.product.dto.result.ProductDetailResult;
import com.picketlogia.picket.api.product.dto.result.SalesProductResult;
import com.picketlogia.picket.api.product.dto.request.ProductSearchRequest;
import com.picketlogia.picket.api.product.dto.request.ProductQueryRequest;
import com.picketlogia.picket.api.product.dto.request.ProductRegisterRequest;
import com.picketlogia.picket.api.product.service.ProductService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 판매자가 공연 상품을 신규 등록한다.
     *
     * @param userAuth 등록 요청자(판매자) 인증 정보
     * @param product  공연 등록 정보가 담긴 요청
     * @param files    업로드할 이미지 파일 목록
     * @return 등록 완료 메시지를 담은 표준 응답
     */
    @PostMapping
    public ResponseEntity<BaseResponse<String>> register(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                         @RequestPart ProductRegisterRequest product,
                                                         @RequestPart List<MultipartFile> files) {

        productService.register(userAuth.getIdx(), product, files);

        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    /**
     * 쿼리 조건에 맞춰 공연 상품 목록을 페이징 조회한다.
     *
     * @param productQueryRequest 페이지·장르·정렬 조건이 담긴 쿼리 요청
     * @return 페이징 결과를 담은 표준 응답
     */
    @GetMapping
    public ResponseEntity<BaseResponse<ProductsResult>> getProducts(ProductQueryRequest productQueryRequest) {

        ProductsResult allByQuery = productService.findAllByQueryPaging(productQueryRequest);

        return ResponseEntity.ok(BaseResponse.success(allByQuery));
    }

    /**
     * 공연 상품 ID로 상세 정보를 조회한다.
     *
     * @param productId 조회할 공연 상품 ID
     * @return 상세 결과를 담은 표준 응답
     */
    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<ProductDetailResult>> getProduct(@PathVariable Long productId) {

        ProductDetailResult findProduct = productService.findProductDetailById(productId);

        return ResponseEntity.ok(BaseResponse.success(findProduct));
    }

    /**
     * 검색어와 정렬 조건으로 판매 중인 공연 상품을 조회한다.
     *
     * @param searchQuery 이름·장르 등 검색 조건
     * @param sort        정렬 키 (옵션)
     * @return 검색 결과를 담은 표준 응답
     */
    @GetMapping("/searchAndSort")
    public ResponseEntity<BaseResponse<List<SalesProductResult>>> searchAndSort(ProductSearchRequest searchQuery,
                                                                                @RequestParam(required = false) String sort) {

        List<SalesProductResult> response = productService.searchAndSort(searchQuery, sort);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
