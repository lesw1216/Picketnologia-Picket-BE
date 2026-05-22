package com.picketlogia.picket.api.user.controller;

import com.picketlogia.picket.api.token.model.TokenCookieNames;
import com.picketlogia.picket.api.token.model.TokenCookiePaths;
import com.picketlogia.picket.api.user.dto.response.SignupResponse;
import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.service.LogoutService;
import com.picketlogia.picket.api.user.service.SignupService;
import com.picketlogia.picket.common.model.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final SignupService signupService;
    private final LogoutService logoutService;

    /**
     * 입력된 회원 정보로 신규 가입을 처리한다.
     *
     * @param register 회원 가입에 필요한 정보가 담긴 요청
     * @return 가입 성공 메시지를 담은 표준 응답
     */
    @PostMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> userForm(@RequestBody UserRegisterRequest register) {

        signupService.signup(register);

        return ResponseEntity.ok(BaseResponse.success("회원 가입 성공"));
    }

    /**
     * 회원 가입 화면에 필요한 메타 정보(성별·회원 유형 목록)를 반환한다.
     *
     * @return 회원 가입 메타 정보를 담은 표준 응답
     */
    @GetMapping("/user/signup")
    public ResponseEntity<BaseResponse<Object>> signup() {

        return ResponseEntity.ok(BaseResponse.success(SignupResponse.from()));
    }

    /**
     * 쿠키에 담긴 refresh/access 토큰을 즉시 만료시켜 로그아웃을 처리한다.
     *
     * @param refreshToken 쿠키에서 추출한 refresh token
     * @param accessToken  쿠키에서 추출한 access token
     * @return 빈 본문과 함께 만료된 쿠키가 담긴 표준 응답
     */
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
