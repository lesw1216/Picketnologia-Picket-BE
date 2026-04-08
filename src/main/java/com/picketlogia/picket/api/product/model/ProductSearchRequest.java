package com.picketlogia.picket.api.product.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchRequest {
    private String name;
    private String genre;
}