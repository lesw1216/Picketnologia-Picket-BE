package com.picketlogia.picket.api.token.controller;

import com.picketlogia.picket.api.token.dto.response.ReissueTokensResponse;
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

    /**
     * 쿠키로 전달된 토큰 쌍을 검증해 access·refresh 토큰을 재발급하고 새 쿠키로 설정한다.
     *
     * @param accessToken  쿠키에서 추출한 기존 access token
     * @param refreshToken 쿠키에서 추출한 기존 refresh token
     * @return 새 토큰 쿠키와 성공 메시지가 담긴 표준 응답
     */
    @PostMapping("/token/reissue")
    public ResponseEntity<BaseResponse<Object>> postReissueToken(@CookieValue(value = TokenCookieNames.ACCESS) String accessToken,
                                                                 @CookieValue(value = TokenCookieNames.REFRESH) String refreshToken) {

        ReissueTokensResponse reissueTokens = tokenService.reissueToken(accessToken, refreshToken);

        if (reissueTokens == null) {
            return ResponseEntity.badRequest().body(BaseResponse.error(BaseResponseStatus.GLOBAL_EXCEPTION));
        }

        ResponseCookie accessTokenCookie = buildAccessTokenCookie(reissueTokens.getAccessToken());
        ResponseCookie refreshTokenCookie = buildRefreshTokenCookie(reissueTokens.getRefreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString(), refreshTokenCookie.toString())
                .body(BaseResponse.success("토큰 재발급 성공"));
    }

    private ResponseCookie buildAccessTokenCookie(String accessTokenValue) {

        return ResponseCookie.from(TokenCookieNames.ACCESS, accessTokenValue)
                .path(TokenCookiePaths.ACCESS)
                .httpOnly(true)
                .build();
    }

    private ResponseCookie buildRefreshTokenCookie(RefreshToken refreshToken) {

        return ResponseCookie.from(TokenCookieNames.REFRESH, refreshToken.getValue())
                .path(TokenCookiePaths.REFRESH)
                .httpOnly(true)
                .maxAge(refreshToken.getExpireSecond())
                .build();
    }
}
