package com.picketlogia.picket.api.auth.service;

import com.picketlogia.picket.api.auth.dto.request.FindEmailRequest;
import com.picketlogia.picket.api.auth.dto.response.FindEmailResponse;
import com.picketlogia.picket.api.auth.dto.request.ResetPasswordRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.repository.UserRepository;
import com.picketlogia.picket.api.user.service.PasswordService;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFindService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordService passwordService;

    /**
     * 이름과 전화번호로 가입된 회원을 찾아 이메일 정보를 반환한다.
     *
     * @param findEmail 이름과 전화번호가 담긴 조회 요청
     * @return 해당 회원의 이메일 응답
     * @throws BaseException 일치하는 회원이 존재하지 않을 때
     */
    public FindEmailResponse findEmailByNameAndPhoneNumber(FindEmailRequest findEmail) {

        User findUser = userRepository.findByNameAndPhoneNumber(findEmail.getName(), findEmail.getPhoneNumber())
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.INVALID_USER_INFO));

        return FindEmailResponse.from(findUser);
    }

    /**
     * 일회용 토큰을 검증한 뒤 해당 회원의 비밀번호를 새 값으로 변경한다.
     *
     * @param resetPassword 토큰과 새 비밀번호가 담긴 요청
     * @throws BaseException 토큰이 무효하거나 회원이 존재하지 않을 때
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPassword) {

        String newPassword = resetPassword.getPassword();
        String token = resetPassword.getToken();

        ResetPasswordRequest findEmailDto = authService.verifyUserPasswordReset(token);

        User findUser = userRepository.findByEmail(findEmailDto.getEmail())
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.INVALID_USER_EMAIL));

        passwordService.changePassword(findUser, newPassword);
    }
}
