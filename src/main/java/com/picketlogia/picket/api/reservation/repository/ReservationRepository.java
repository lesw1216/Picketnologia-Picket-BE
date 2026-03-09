package com.picketlogia.picket.api.reservation.repository;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Long countByUserAndProduct(User user, Product product);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.product WHERE r.paymentIdx = :paymentIdx")
    Optional<Reservation> findByPaymentIdx(String paymentIdx);


    List<Reservation> findByUserIdxAndCreatedAtBetween(Long userIdx, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT r.paymentStatus FROM Reservation r WHERE r.paymentIdx = :paymentId AND r.user.idx = :userIdx")
    Optional<PaymentStatus> findStatusByPaymentIdxAndUserId(String paymentId, Long userIdx);
}
