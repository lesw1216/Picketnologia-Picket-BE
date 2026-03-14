package com.picketlogia.picket.api.token.controller;

import com.picketlogia.picket.api.token.dto.ReissueTokens;
import com.picketlogia.picket.api.token.model.RefreshToken;
import com.picketlogia.picket.api.token.model.TokenCookieNames;
import com.picketlogia.picket.api.token.model.TokenCookiePaths;
import com.picketlogia.picket.api.token.service.TokenService;
import com.picketlogia.picket.common.model.BaseResponse;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenReissueController {

    private final TokenService tokenService;

    @PostMapping("/token/reissue")
    public ResponseEntity<BaseResponse<Object>> postReissueToken(@CookieValue(value = TokenCookieNames.ACCESS) String accessToken,
                                                                 @CookieValue(value = TokenCookieNames.REFRESH) String refreshToken) {

        // 토큰 재발급 요청
        ReissueTokens reissueTokens = tokenService.reissueToken(accessToken, refreshToken);

        // 토큰 재발급 실패
        if (reissueTokens == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error(BaseResponseStatus.GLOBAL_EXCEPTION));
        }

        String reissueAccessToken = reissueTokens.getAccessToken();
        RefreshToken reissueRefreshToken = reissueTokens.getRefreshToken();

        // access token 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from(TokenCookieNames.ACCESS, reissueAccessToken)
                .path(TokenCookiePaths.ACCESS)
                .httpOnly(true)
                .build();

        // refresh token 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from(TokenCookieNames.REFRESH, reissueRefreshToken.getValue())
                .path(TokenCookiePaths.REFRESH)
                .httpOnly(true)
                .maxAge(reissueRefreshToken.getExpireSecond())
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(BaseResponse.success("토큰 재발급 성공"));
    }
}
