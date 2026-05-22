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
     * Token을 재발급 합니다.
     * @param requestAccessToken 요청으로 받은 Access Token
     * @param refreshToken 요청으로 받은 Refresh Token
     * @return <code>ReissueTokensResponse</code>
     */
    public ReissueTokensResponse reissueToken(String requestAccessToken, String refreshToken) {
        /*
         * Legacy flow before Lua script:
         * 1. find access token by refresh token
         * 2. compare request access token with stored access token
         * 3. issue new tokens
         * 4. save new refresh token -> access token
         * 5. delete previous refresh token
         *
         * This flow had a race condition because Redis read/compare/delete/save
         * happened across multiple commands.
         */

        String reIssuedAccessToken = AccessToken.reIssue(requestAccessToken);
        RefreshToken reIssuedRefreshToken = RefreshToken.issue();

        // Changed: token rotation is now executed atomically inside Redis via Lua script.
        // Lua script를 사용하여 토큰 재발급 로직을 하나의 작업으로 처리할 수 있다.
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

    public RefreshToken issueRefreshToken() {
        return RefreshToken.issue();
    }

    public void saveRefreshTokenAndAccessToken(RefreshToken refreshToken, String accessToken) {

        TokensResponse tokens = TokensResponse.from(refreshToken, accessToken);
        tokenRepository.save(tokens);
    }
}
