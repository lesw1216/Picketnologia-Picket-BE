package com.picketlogia.picket.api.token.dto;

import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tokens {

    private RefreshToken refreshToken;
    private String accessToken;

    public static Tokens from(RefreshToken refreshToken, String accessToken) {

        return Tokens.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }
}
