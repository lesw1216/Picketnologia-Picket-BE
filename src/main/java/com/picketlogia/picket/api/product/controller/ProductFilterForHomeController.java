package com.picketlogia.picket.api.product.controller;

import com.picketlogia.picket.api.product.dto.result.SalesProductResult;
import com.picketlogia.picket.api.product.dto.result.UpcomingProductResult;
import com.picketlogia.picket.api.product.service.ProductService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home/products")
@RequiredArgsConstructor
public class ProductFilterForHomeController {

    private final ProductService productService;

    @GetMapping("/best-sellers")
    public ResponseEntity<BaseResponse<List<SalesProductResult>>> getBestSellers(@RequestParam String genre) {

        List<SalesProductResult> top10Products = productService.findTop5ByGenreOrderBySalesCount(genre);
        return ResponseEntity.ok(BaseResponse.success(top10Products));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<BaseResponse<List<UpcomingProductResult>>> getOpenProductsTop5() {

        List<UpcomingProductResult> upcomingProducts = productService.findUpcomingProducts();
        return ResponseEntity.ok(BaseResponse.success(upcomingProducts));
    }
}
