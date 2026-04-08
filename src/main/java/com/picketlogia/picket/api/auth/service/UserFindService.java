package com.picketlogia.picket.api.auth.service;

import com.picketlogia.picket.api.auth.dto.request.FindEmailRequest;
import com.picketlogia.picket.api.auth.dto.response.FindEmailResponse;
import com.picketlogia.picket.api.auth.dto.request.ResetPasswordRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.repository.UserRepository;
import com.picketlogia.picket.api.user.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFindService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordService passwordService;

    public FindEmailResponse findEmailByNameAndPhoneNumber(FindEmailRequest findEmail) {

        Optional<User> result = userRepository.findByNameAndPhoneNumber(findEmail.getName(), findEmail.getPhoneNumber());

        if (result.isPresent()) {
            User findUser = result.get();
            return FindEmailResponse.from(findUser);
        }

        return null;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPassword) {

        String newPassword = resetPassword.getPassword();

        String token = resetPassword.getToken();

        ResetPasswordRequest findEmailDto = authService.verifyUserPasswordReset(token);

        Optional<User> result = userRepository.findByEmail(findEmailDto.getEmail());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 이메일");
        }

        User findUser = result.get();
        passwordService.changePassword(findUser, newPassword);
    }
}
