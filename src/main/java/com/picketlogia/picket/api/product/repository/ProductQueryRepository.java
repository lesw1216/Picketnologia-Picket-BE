package com.picketlogia.picket.api.product.repository;

import com.picketlogia.picket.api.product.dto.request.ProductSearchRequest;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.model.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QProduct product;

    public ProductQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.product = QProduct.product;
    }

    public List<Product> searchAndSort(ProductSearchRequest dto, String sort) {
        JPAQuery<Product> query = queryFactory
                .selectFrom(product)
                .leftJoin(product.productImage).fetchJoin() // 검색할때 이미지 조회 N+1문제 발생해 fetchjoin 추가
                .where(
                        //이름으로 검색
                        nameContains(dto.getName()),
                        //장르로 검색
                        genreEquals(dto.getGenre())
                );


        // 동적 정렬 조건 처리
        //sort="rating" 이렇게 오면 리뷰평균점수로 내림차순
        if ("rating".equals(sort)) {
            query.orderBy(product.reviewRating.desc());
        }//sort="reivew" 이렇게 오면 리뷰갯수로 내림차순
        else if ("review".equals(sort)) {
            query.orderBy(product.reviewCount.desc());
        } else {
            query.orderBy(product.idx.asc()); // 기본값 (최신순)
        }

        return query.fetch();
    }


    private boolean hasText(String str) {
        return str != null && !str.isBlank();
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? product.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression genreEquals(String genre) {
        return hasText(genre) ? product.genre.code.equalsIgnoreCase(genre) : null;
    }
}



