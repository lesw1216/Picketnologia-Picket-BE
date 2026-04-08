package com.picketlogia.picket.api.user.model.entity;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.user.model.enums.Gender;
import com.picketlogia.picket.api.user.model.enums.UserType;
import com.picketlogia.picket.common.model.BaseEntity;
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
@Table(name = "users")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    // 회원 상태
    @ManyToOne
    @JoinColumn(name = "user_status_id", nullable = false)
    private UserStatus userStatus;

    // 회원 역할
    @ManyToOne
    @JoinColumn(name = "user_role_id", nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "user")
    private List<Product> products;

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
