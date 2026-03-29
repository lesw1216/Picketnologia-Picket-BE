package com.picketlogia.picket.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.api.user.model.dto.UserAuth;
import com.picketlogia.picket.common.model.BaseResponse;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import com.picketlogia.picket.api.token.model.AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : request.getCookies()) {

                // Cookie 리스트에 JWT 토큰이 있는지 확인
                if (cookie.getName().equals(AccessToken.TOKEN_NAME)) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken != null) {

            // JWT 토큰에 저장된 Claims 얻기
            Claims claims;

            try {

                claims = AccessToken.getClaims(accessToken);

                if (claims != null) {

                    String email = AccessToken.getValue(claims, AccessToken.EMAIL_NAME);
                    Long id = Long.parseLong(AccessToken.getValue(claims, AccessToken.IDX_NAME));
                    String role = AccessToken.getValue(claims, AccessToken.ROLE_NAME);
                    String userType = AccessToken.getValue(claims, AccessToken.USER_TYPE_NAME);

                    UserAuth authUser = UserAuth.builder()
                            .idx(id)
                            .email(email)
                            .build();

                    // Authentication 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            authUser,
                            null,
                            List.of(new SimpleGrantedAuthority(role), new SimpleGrantedAuthority(userType))
                    );

                    // SecurityContext에 Authentication 객체 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (ExpiredJwtException e) {
                Cookie cookie = new Cookie(AccessToken.TOKEN_NAME, null);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);

                response.addCookie(cookie);
                response.setContentType("application/json; charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(
                        new ObjectMapper().writeValueAsString(
                                BaseResponse.error(BaseResponseStatus.EXPIRED_JWT)
                        )
                );

                log.error(e.getMessage());

                return;
            }


        }

        filterChain.doFilter(request, response);
    }
}
