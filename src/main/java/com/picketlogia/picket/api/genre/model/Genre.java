package com.picketlogia.picket.api.genre.model;

import com.picketlogia.picket.api.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_genre_code", columnList = "code")
        }
)
public class Genre {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    private List<Product> products;
}
