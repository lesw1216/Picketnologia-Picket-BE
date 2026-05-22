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

    @PostMapping
    public ResponseEntity<BaseResponse<String>> register(@AuthenticationPrincipal UserAuthRequest userAuth,
                                                         @RequestPart ProductRegisterRequest product,
                                                         @RequestPart List<MultipartFile> files) {

        productService.register(userAuth.getIdx(), product, files);
        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<ProductsResult>> getProducts(ProductQueryRequest productQueryRequest) {

        ProductsResult allByQuery = productService.findAllByQueryPaging(productQueryRequest);
        return ResponseEntity.ok(BaseResponse.success(allByQuery));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<ProductDetailResult>> getProduct(@PathVariable Long productId) {

        ProductDetailResult findProduct = productService.findProductDetailById(productId);
        return ResponseEntity.ok(BaseResponse.success(findProduct));
    }

    @GetMapping("/searchAndSort")
    public ResponseEntity<BaseResponse<List<SalesProductResult>>> searchAndSort(ProductSearchRequest searchQuery,
                                                                                @RequestParam(required = false) String sort) {

        List<SalesProductResult> response = productService.searchAndSort(searchQuery, sort);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}