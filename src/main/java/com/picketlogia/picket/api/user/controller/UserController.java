package com.picketlogia.picket.api.user.controller;

import com.picketlogia.picket.api.token.model.TokenCookieNames;
import com.picketlogia.picket.api.token.model.TokenCookiePaths;
import com.picketlogia.picket.api.user.dto.response.SignupResponse;
import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.service.LogoutService;
import com.picketlogia.picket.api.user.service.SignupService;
import com.picketlogia.picket.common.model.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name="유저 기능")
public class UserController {

    private final SignupService signupService;
    private final LogoutService logoutService;

    @Operation(
            summary = "회원가입 한다",
            description = "유저 정보 입력받고 저장한다."
    )
    @PostMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> userForm(@RequestBody UserRegisterRequest register) {
        signupService.signup(register);

        return ResponseEntity.ok(BaseResponse.success("회원 가입 성공"));
    }

    @Operation(
            summary = "판매자 권한 확인",
            description = "유저의 권환을 확인한다."
    )
    @GetMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> signup() {
        return ResponseEntity.ok(BaseResponse.success(SignupResponse.from()));
    }

    @Operation(
            summary = "로그아웃 기능",
            description = "쿠키 유효시간 정보 덮어씌워 쿠키를 삭제한다."
    )
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Object>> logout(@CookieValue(value = TokenCookieNames.REFRESH) String refreshToken,
                                                       @CookieValue(value = TokenCookieNames.ACCESS) String accessToken) {

        logoutService.logout(refreshToken, accessToken);

        ResponseCookie deleteAccessTokenCookie = ResponseCookie.from(TokenCookieNames.ACCESS)
                .maxAge(0)
                .path(TokenCookiePaths.ACCESS)
                .build();

        ResponseCookie deleteRefreshTokenCookie = ResponseCookie.from(TokenCookieNames.REFRESH)
                .maxAge(0)
                .path(TokenCookiePaths.REFRESH)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", deleteAccessTokenCookie.toString(), deleteRefreshTokenCookie.toString())
                .body(BaseResponse.success(null));
    }
}
