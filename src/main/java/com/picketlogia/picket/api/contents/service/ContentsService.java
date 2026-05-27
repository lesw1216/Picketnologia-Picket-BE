package com.picketlogia.picket.api.contents.service;

import com.picketlogia.picket.api.contents.dto.ContentsResponse;
import com.picketlogia.picket.api.product.dto.result.UpcomingProductResult;
import com.picketlogia.picket.api.product.dto.result.ProductsResult;
import com.picketlogia.picket.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ProductService productService;

    /**
     * 장르 코드를 받아 해당 장르의 공연 목록과 오픈 예정 공연을 조합해 반환한다.
     *
     * @param genre 장르 코드
     * @return 콘텐츠 응답
     */
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
