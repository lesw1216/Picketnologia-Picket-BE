package com.picketlogia.picket.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.api.token.model.AccessToken;
import com.picketlogia.picket.api.token.model.RefreshToken;
import com.picketlogia.picket.api.token.model.TokenCookieNames;
import com.picketlogia.picket.api.token.model.TokenCookiePaths;
import com.picketlogia.picket.api.token.service.TokenService;
import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.api.user.dto.response.UserLoginResponse;
import com.picketlogia.picket.common.model.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserAuthRequest authUser = (UserAuthRequest) authentication.getPrincipal();

        String accessToken = AccessToken.issue(
                authUser.getEmail(),
                authUser.getIdx(),
                authUser.getRole(),
                authUser.getUserType()
        );

        if (accessToken != null) {

            RefreshToken issuedRefreshToken = issueRefreshToken(accessToken);
            String refreshTokenValue = issuedRefreshToken.getValue();
            Integer refreshTokenMaxAge = issuedRefreshToken.getExpireSecond();

            // Create Cookie of accessToken
            Cookie accessTokenCookie = new Cookie(AccessToken.TOKEN_NAME, accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath(TokenCookiePaths.ACCESS);

            // Create Cookie of refreshToken
            Cookie refreshTokenCookie = new Cookie(TokenCookieNames.REFRESH, refreshTokenValue);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath(TokenCookiePaths.REFRESH);
            refreshTokenCookie.setMaxAge(refreshTokenMaxAge);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            BaseResponse.success(UserLoginResponse.from(authUser))
                    )
            );
        }
    }

    private RefreshToken issueRefreshToken(String accessToken) {
        RefreshToken refreshToken = tokenService.issueRefreshToken();
        tokenService.saveRefreshTokenAndAccessToken(refreshToken, accessToken);
        return refreshToken;
    }
}
