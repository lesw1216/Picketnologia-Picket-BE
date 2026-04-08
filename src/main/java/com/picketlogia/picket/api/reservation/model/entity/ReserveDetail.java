package com.picketlogia.picket.api.reservation.model.entity;

import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ROUND_TIME_SEAT_UNIQUE",
                        columnNames = {"round_time_idx", "seat_idx"}
                )
        }
)
public class ReserveDetail extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_idx", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_time_idx", nullable = false)
    private RoundTime roundTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_idx", nullable = false)
    private Seat seat;
}
