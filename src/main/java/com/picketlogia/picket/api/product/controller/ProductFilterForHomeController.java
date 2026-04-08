package com.picketlogia.picket.api.product.controller;

import com.picketlogia.picket.api.product.model.SalesProductResult;
import com.picketlogia.picket.api.product.model.UpcomingProductResult;
import com.picketlogia.picket.api.product.service.ProductService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "홈 화면을 위한 공연 기능 관련 컨트롤러")
public class ProductFilterForHomeController {

    private final ProductService productService;

    @Operation(
            summary = "홈 화면에서 보여줄 공연 조회입니다.",
            description = "장르별 5위권 랭킹 공연 목록을 조회합니다. 랭킹의 기준은 판매순입니다."
    )
    @GetMapping("/best-sellers")
    public ResponseEntity<BaseResponse<List<SalesProductResult>>> getBestSellers(@RequestParam String genre) {

        List<SalesProductResult> top10Products = productService.findTop5ByGenreOrderBySalesCount(genre);
        return ResponseEntity.ok(BaseResponse.success(top10Products));
    }

    @Operation(
            summary = "오픈 예정일 공연 조회",
            description = "장르 구분 없이 오픈 예정일이 빠른 순으로 5개의 공연을 조회합니다."
    )
    @GetMapping("/upcoming")
    public ResponseEntity<BaseResponse<List<UpcomingProductResult>>> getOpenProductsTop5() {

        List<UpcomingProductResult> upcomingProducts = productService.findUpcomingProducts();
        return ResponseEntity.ok(BaseResponse.success(upcomingProducts));
    }
}
