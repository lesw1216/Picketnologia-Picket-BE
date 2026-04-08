package com.picketlogia.picket.api.product.repository;

import com.picketlogia.picket.api.product.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
}
