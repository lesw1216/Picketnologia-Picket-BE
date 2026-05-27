package com.picketlogia.picket.api.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductQueryRequest {
    private Integer page;
    private String genre;
    private String sort;
}

