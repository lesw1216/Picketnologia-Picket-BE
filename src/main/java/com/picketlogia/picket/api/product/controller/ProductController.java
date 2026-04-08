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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name="상품 등록 & 조회 기능")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "상품 정보 등록",
            description = "등록한 상품의 정보를 저장한다."
    )
    @PostMapping
    public ResponseEntity<BaseResponse<String>> register(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                         @RequestPart ProductRegisterRequest product,
                                                         @RequestPart List<MultipartFile> files) {

        productService.register(userAuth.getIdx(), product, files);
        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    @Operation(
            summary = "상품조회 -  productQuery에 맞는 상품들 조회",
            description = "쿼리를 사용한 상품 목록 조회하는 기능."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<ProductsResult>> getProducts(ProductQueryRequest productQueryRequest) {

        ProductsResult allByQuery = productService.findAllByQueryPaging(productQueryRequest);
        return ResponseEntity.ok(BaseResponse.success(allByQuery));
    }

    // 상품 상세 조회
    @Operation(
            summary = "상품상세 조회  - productId에 해당하는 상품 조회 ",
            description = "요청받은 productId에 해당하는 상품을 조회한다."
    )
    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<ProductDetailResult>> getProduct(@PathVariable Long productId) {

        ProductDetailResult findProduct = productService.findProductDetailById(productId);
        return ResponseEntity.ok(BaseResponse.success(findProduct));
    }

    @Operation(
            summary = "상품 조건 검색 & 정렬 ",
            description = "RequestParam 조건에 맞게 이름 포함 검색 ,리뷰 평균 정렬 , 리뷰 갯수정렬 "
    )
    @GetMapping("/searchAndSort")
    public ResponseEntity<BaseResponse<List<SalesProductResult>>> searchAndSort(ProductSearchRequest searchQuery,
                                                                                @RequestParam(required = false) String sort) {

        List<SalesProductResult> response = productService.searchAndSort(searchQuery, sort);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}