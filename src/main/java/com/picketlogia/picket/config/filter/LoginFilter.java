package com.picketlogia.picket.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.api.user.model.dto.login.UserLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler loginSuccessHandler) {

        super(authenticationManager);
        setAuthenticationSuccessHandler(loginSuccessHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken;
        try {

            UserLogin dto = objectMapper.readValue(request.getInputStream(), UserLogin.class);
            authToken = new UsernamePasswordAuthenticationToken(
                    dto.getEmail(), dto.getPassword(), null
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.getAuthenticationManager().authenticate(authToken);
    }
}
