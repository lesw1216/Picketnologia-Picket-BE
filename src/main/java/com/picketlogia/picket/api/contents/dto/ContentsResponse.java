package com.picketlogia.picket.api.contents.dto;

import com.picketlogia.picket.api.product.model.ProductReadForList;
import com.picketlogia.picket.api.product.model.ProductReadForUpcoming;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentsResponse {

    private Integer currentPage;
    private Integer totalPage;
    private List<ProductReadForList> products;
    private List<ProductReadForUpcoming> upcomingPerformances;

    public static ContentsResponse from(List<ProductReadForList> products,
                                        Integer currentPage,
                                        Integer totalPage, List<ProductReadForUpcoming> upcomingPerformances) {

        return ContentsResponse.builder()
                .products(products)
                .currentPage(currentPage)
                .totalPage(totalPage)
                .upcomingPerformances(upcomingPerformances)
                .build();

    }
}
