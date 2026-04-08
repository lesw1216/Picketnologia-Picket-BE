package com.picketlogia.picket.api.seat.repository;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {

    @Query("SELECT s FROM SeatGrade s WHERE s.product = :product")
    List<SeatGrade> findAllByProduct(Product product);
}
