package com.picketlogia.picket.api.user.controller;

import com.picketlogia.picket.api.user.model.dto.signup.SignupResp;
import com.picketlogia.picket.api.user.model.dto.signup.UserRegister;
import com.picketlogia.picket.api.user.service.SignupService;
import com.picketlogia.picket.common.model.BaseResponse;
import com.picketlogia.picket.api.token.model.AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name="유저 기능")
public class UserController {

    private final SignupService signupService;

    @Operation(
            summary = "회원가입 한다",
            description = "유저 정보 입력받고 저장한다."
    )
    @PostMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> userForm(@RequestBody UserRegister register) {
        signupService.signup(register);

        return ResponseEntity.ok(BaseResponse.success("회원 가입 성공"));
    }

    @Operation(
            summary = "판매자 권한 확인",
            description = "유저의 권환을 확인한다."
    )
    @GetMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> signup() {
        return ResponseEntity.ok(BaseResponse.success(SignupResp.from()));
    }

    @Operation(
            summary = "로그아웃 기능",
            description = "쿠키 유효시간 정보 덮어씌워 쿠키를 삭제한다."
    )
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Object>> logout() {

        ResponseCookie responseCookie = ResponseCookie.from(AccessToken.TOKEN_NAME, null)
                .httpOnly(true)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(BaseResponse.success(null));
    }
}
