package com.picketlogia.picket.api.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequest {
    private String name;
    private String genre;
}
