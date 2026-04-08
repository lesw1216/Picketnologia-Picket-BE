package com.picketlogia.picket.api.contents.service;

import com.picketlogia.picket.api.contents.dto.ContentsResponse;
import com.picketlogia.picket.api.product.model.UpcomingProductResult;
import com.picketlogia.picket.api.product.model.ProductsResult;
import com.picketlogia.picket.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ProductService productService;

    public ContentsResponse findContents(String genre) {

        ProductsResult findProducts = productService.findAllByGenre(genre);

        List<UpcomingProductResult> upcomingProducts =
                productService.findUpcomingProductsByGenreCode(genre);

        return ContentsResponse.from(
                findProducts.getProducts(),
                findProducts.getCurrentPage(),
                findProducts.getTotalPage(),
                upcomingProducts
        );
    }

}
