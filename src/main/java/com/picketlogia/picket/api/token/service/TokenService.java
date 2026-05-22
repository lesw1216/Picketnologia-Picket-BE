package com.picketlogia.picket.api.token.service;

import com.picketlogia.picket.api.token.dto.response.ReissueTokensResponse;
import com.picketlogia.picket.api.token.dto.response.TokensResponse;
import com.picketlogia.picket.api.token.model.AccessToken;
import com.picketlogia.picket.api.token.model.RefreshToken;
import com.picketlogia.picket.api.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    /**
     * Lua script로 access·refresh 토큰 쌍을 원자적으로 회전(rotate)시킨다.
     *
     * @param requestAccessToken 요청으로 받은 기존 access token
     * @param refreshToken       요청으로 받은 기존 refresh token
     * @return 재발급된 토큰 쌍, 회전 실패(미일치·만료) 시 {@code null}
     */
    public ReissueTokensResponse reissueToken(String requestAccessToken, String refreshToken) {

        String reIssuedAccessToken = AccessToken.reIssue(requestAccessToken);
        RefreshToken reIssuedRefreshToken = RefreshToken.issue();

        long reissueResult = tokenRepository.reissueTokensAtomically(
                refreshToken,
                requestAccessToken,
                reIssuedRefreshToken,
                reIssuedAccessToken,
                reIssuedRefreshToken.getExpireMillis()
        );

        if (reissueResult == 1L) {
            return ReissueTokensResponse.from(reIssuedAccessToken, reIssuedRefreshToken);
        }

        return null;
    }

    /**
     * 새 refresh token을 발급해 반환한다.
     *
     * @return 신규 발급된 refresh token
     */
    public RefreshToken issueRefreshToken() {

        return RefreshToken.issue();
    }

    /**
     * refresh token을 key, access token을 value로 Redis에 저장한다.
     *
     * @param refreshToken 저장할 refresh token
     * @param accessToken  저장할 access token 문자열
     */
    public void saveRefreshTokenAndAccessToken(RefreshToken refreshToken, String accessToken) {

        TokensResponse tokens = TokensResponse.from(refreshToken, accessToken);
        tokenRepository.save(tokens);
    }
}
