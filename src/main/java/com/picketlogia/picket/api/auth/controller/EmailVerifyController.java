package com.picketlogia.picket.api.auth.controller;

import com.picketlogia.picket.api.auth.dto.request.AuthCodeMailRequest;
import com.picketlogia.picket.api.auth.service.AuthService;
import com.picketlogia.picket.api.auth.service.mail.AuthCodeMailService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerifyController {

    private final AuthCodeMailService authCodeMailService;
    private final AuthService authService;

    /**
     * 회원가입 단계에서 입력한 이메일로 인증 번호 메일을 발송한다.
     *
     * @param email 인증 번호를 받을 이메일 주소
     * @return 전송 성공 메시지를 담은 표준 응답
     */
    @GetMapping("/email/verify-code")
    public ResponseEntity<BaseResponse<Object>> sendAuthCode(String email) {

        authCodeMailService.sendToEmail(email);

        return ResponseEntity.ok(BaseResponse.success("인증 번호 전송 성공"));
    }

    /**
     * 사용자가 입력한 인증 번호가 발송된 코드와 일치하는지 검증한다.
     *
     * @param authCodeMail 이메일과 인증 코드가 담긴 검증 요청
     * @return 검증 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/email/verify-code")
    public ResponseEntity<BaseResponse<Object>> verifyCode(@RequestBody AuthCodeMailRequest authCodeMail) {

        authService.verifyAuthCode(authCodeMail);

        return ResponseEntity.ok(BaseResponse.success("이메일 인증 성공"));
    }
}
