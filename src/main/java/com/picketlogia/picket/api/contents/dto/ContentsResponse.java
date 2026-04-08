package com.picketlogia.picket.api.contents.dto;

import com.picketlogia.picket.api.product.model.SalesProductResult;
import com.picketlogia.picket.api.product.model.UpcomingProductResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentsResponse {

    private Integer currentPage;
    private Integer totalPage;
    private List<SalesProductResult> products;
    private List<UpcomingProductResult> upcomingPerformances;

    public static ContentsResponse from(List<SalesProductResult> products,
                                        Integer currentPage,
                                        Integer totalPage, List<UpcomingProductResult> upcomingPerformances) {

        return ContentsResponse.builder()
                .products(products)
                .currentPage(currentPage)
                .totalPage(totalPage)
                .upcomingPerformances(upcomingPerformances)
                .build();

    }
}
