package com.picketlogia.picket.api.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQueryRequest {
    private Integer page;
    private String genre;
    private String sort;
}
