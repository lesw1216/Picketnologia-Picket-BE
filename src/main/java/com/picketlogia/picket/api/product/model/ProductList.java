package com.picketlogia.picket.api.product.model;

import com.picketlogia.picket.api.product.model.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductList {
    private List<SalesProductResult> productList;

    public static ProductList from(List<Product> products) {
        return ProductList.builder()
                .productList(products.stream().map(SalesProductResult::from).toList())
                .build();
    }
}
