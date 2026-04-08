package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserService userService;
    private final SellerService sellerService;

    public void signup(UserRegisterRequest register) {
        User savedUser = userService.signup(register);

        if (UserType.SELLER.equals(register.getUserType())) {
            sellerService.save(register, savedUser.getIdx());
        }
    }
}
