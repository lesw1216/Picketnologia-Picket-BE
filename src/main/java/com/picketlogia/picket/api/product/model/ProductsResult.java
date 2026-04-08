package com.picketlogia.picket.api.product.model;

import com.picketlogia.picket.api.product.model.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductsResult {

    private Integer currentPage;
    private Integer totalPage;
    private List<SalesProductResult> products;

    public static ProductsResult from(List<Product> products, Integer currentPage, Integer totalPages) {
        return ProductsResult.builder()
                .products(products.stream().map(SalesProductResult::from).toList())
                .currentPage(currentPage)
                .totalPage(totalPages)
                .build();
    }
}
