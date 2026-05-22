package com.picketlogia.picket.api.token.dto.response;

import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokensResponse {

    private RefreshToken refreshToken;
    private String accessToken;

    public static TokensResponse from(RefreshToken refreshToken, String accessToken) {

        return TokensResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }
}
