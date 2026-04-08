package com.picketlogia.picket.api.user.dto.request;

import com.picketlogia.picket.api.user.model.entity.Seller;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.model.entity.UserRole;
import com.picketlogia.picket.api.user.model.entity.UserStatus;
import com.picketlogia.picket.api.user.model.enums.Gender;
import com.picketlogia.picket.api.user.model.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class UserRegisterRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Min(value = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Max(value = 15, message = "닉네임은 15자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]{2,30}$", message = "이름은 한글 또는 영문으로 2~30자 이내여야 합니다.")
    private String name;

    @NotBlank(message = "생년월일을 입력해주세요.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식이 올바르지 않습니다.")
    private String birth;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotBlank(message = "유저 타입을 선택해주세요.")
    private UserType userType;

    @NotBlank(message = "성별을 선택해주세요.")
    private Gender gender;

    // 대표 이름: 공백 불가, 한글/영문만 허용, 길이 제한
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]{2,30}$", message = "대표자 이름은 한글 또는 영문으로 2~30자 이내여야 합니다.")
    private String representativeName;

    // 사업자등록번호: 숫자 10자리, 000-00-00000 또는 0000000000 형태 허용
    @Pattern(regexp = "^(\\d{3}-\\d{2}-\\d{5}|\\d{10})$", message = "사업자등록번호는 10자리 숫자 또는 XXX-XX-XXXXX 형식이어야 합니다.")
    private String businessNumber;

    // 회사 주소: 공백 불가, 길이 제한
    @Size(min = 5, max = 100, message = "회사 주소는 5자 이상 100자 이하여야 합니다.")
    private String businessAddress;

    public User toUserEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .name(name)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .userStatus(
                        UserStatus.builder()
                                .idx(1)
                                .build()
                )
                .userRole(
                        UserRole.builder()
                                .idx(2)
                                .build()
                )
                .userType(userType)
                .gender(gender)
                .build();
    }

    public Seller toSellerEntity(Long userId) {
        return Seller.builder()
                .businessAddress(businessAddress)
                .businessNumber(businessNumber)
                .representativeName(representativeName)
                .user(
                        User.builder()
                                .idx(userId)
                                .build()
                )
                .build();
    }
}