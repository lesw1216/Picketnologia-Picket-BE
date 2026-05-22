package com.picketlogia.picket.api.auth.controller;

import com.picketlogia.picket.api.auth.dto.request.FindEmailRequest;
import com.picketlogia.picket.api.auth.dto.response.FindEmailResponse;
import com.picketlogia.picket.api.auth.dto.request.ResetPasswordRequest;
import com.picketlogia.picket.api.auth.service.UserFindService;
import com.picketlogia.picket.api.auth.service.mail.PasswordResetMailService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserFindController {

    private final UserFindService userFindService;
    private final PasswordResetMailService passwordResetMailService;

    /**
     * 이름과 전화번호로 회원을 조회해 이메일을 찾아 반환한다.
     *
     * @param dto 이름과 전화번호가 담긴 조회 요청
     * @return 조회된 이메일을 담은 표준 응답
     */
    @PostMapping("/find-email")
    public ResponseEntity<BaseResponse<Object>> findEmail(@RequestBody FindEmailRequest dto) {

        FindEmailResponse findUser = userFindService.findEmailByNameAndPhoneNumber(dto);

        return ResponseEntity.ok(BaseResponse.success(findUser));
    }

    /**
     * 비밀번호 재설정 링크를 입력된 이메일 주소로 발송한다.
     *
     * @param emailForPwdRest 이메일 주소가 담긴 요청
     * @return 전송 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/find-password/link")
    public ResponseEntity<BaseResponse<Object>> sendAuthCodeForResetPassword(@RequestBody ResetPasswordRequest emailForPwdRest) {

        String email = emailForPwdRest.getEmail();
        passwordResetMailService.sendToEmail(email);

        return ResponseEntity.ok(BaseResponse.success("비밀번호 재설정 링크 전송 성공"));
    }

    /**
     * 비밀번호 재설정 토큰을 검증한 뒤 새 비밀번호로 변경한다.
     *
     * @param dto 토큰과 새 비밀번호가 담긴 요청
     * @return 재설정 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Object>> resetPassword(@RequestBody ResetPasswordRequest dto) {

        userFindService.resetPassword(dto);

        return ResponseEntity.ok(BaseResponse.success("재설정 성공"));
    }
}
