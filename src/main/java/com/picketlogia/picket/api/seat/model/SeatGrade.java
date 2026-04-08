package com.picketlogia.picket.api.seat.model;

import com.picketlogia.picket.api.product.model.Product;
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
public class SeatGrade {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatGradeStatus grade;

    @Column(nullable = false)
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
