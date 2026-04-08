package com.picketlogia.picket.api.product.repository;

import com.picketlogia.picket.api.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByIdx(Long idx);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage " +
            "WHERE p.genre.code = :code AND p.openDate > :now " +
            "ORDER BY p.openDate ASC")
    List<Product> findTop5ByGenre_CodeAndOpenDateAfterOrderByOpenDateAsc(String code,LocalDateTime now);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage " +
            "WHERE p.genre.code = :code AND p.openDate > :now")
    List<Product> findTop5ByGenre_CodeOrderByOpenDateDescPage(String code, LocalDateTime now, Pageable pageable);

    List<Product> findTop5ByOpenDateAfterOrderByOpenDateAsc(LocalDateTime now);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage WHERE p.genre.code = :code")
    Page<Product> findByGenre_Code(String code, Pageable pageable);

//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage WHERE p.genre.code = :code order by p.reviewCount desc")
//    Page<Product> findByGenre_CodeOrderByReviewCountDesc(String code, Pageable pageable);
//
//    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage WHERE p.genre.code = :code order by p.reviewRating desc")
//    Page<Product> findByGenre_CodeOrderByReviewRatingDesc(String code, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImage WHERE p.genre.code = :code order by p.createdAt desc")
    Page<Product> findByGenre_CodeOrderByCreatedAtDesc(String code, Pageable pageable);

    List<Product> findTop5ByGenre_CodeOrderBySalesCountDesc(String code);
}