package com.picketlogia.picket.api.token.dto;

import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueTokens {

    private String accessToken;
    private RefreshToken refreshToken;

    public static ReissueTokens from(String newAccessToken, RefreshToken newRefreshToken) {
        return ReissueTokens.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}