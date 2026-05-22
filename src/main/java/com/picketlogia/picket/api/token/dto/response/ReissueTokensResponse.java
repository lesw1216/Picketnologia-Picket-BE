package com.picketlogia.picket.api.token.dto.response;

import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueTokensResponse {

    private String accessToken;
    private RefreshToken refreshToken;

    public static ReissueTokensResponse from(String newAccessToken, RefreshToken newRefreshToken) {
        return ReissueTokensResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
