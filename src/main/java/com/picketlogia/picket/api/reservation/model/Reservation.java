package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.reservation.dto.request.UpdateReservationRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_reservation_payment_idx", columnList = "payment_idx")
})
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = true)
    private Long price;

    @Column(nullable = false, unique = true)
    private String paymentIdx;

    @Column(nullable = true)
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToMany(mappedBy = "reservation" ,fetch = FetchType.LAZY)
    private List<ReserveDetail> reserveDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    private Product product;

    public void completeReservation(UpdateReservationRequest update) {
        this.price = update.getPrice();
        this.paidAt = update.getPaidAt();
        this.paymentStatus = update.getPaymentStatus();
    }
}