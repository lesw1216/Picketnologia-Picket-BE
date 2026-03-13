package com.picketlogia.picket.api.token.service;

import com.picketlogia.picket.api.token.dto.ReissueTokens;
import com.picketlogia.picket.api.token.dto.Tokens;
import com.picketlogia.picket.api.token.model.RefreshToken;
import com.picketlogia.picket.api.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public ReissueTokens reissueToken(String accessToken, String refreshToken) {

        String findAccessToken = tokenRepository.findTokens(refreshToken);

        isExistRefreshToken(findAccessToken);

        if (findAccessToken.equals(accessToken)) {
            // 재발급
            // redis 저장
        } else {
            // 재발급 실패 400 반환 필요
        }

        return null;
    }

    public RefreshToken issueRefreshToken() {
        return RefreshToken.issue();
    }

    public void saveRefreshTokenAndAccessToken(RefreshToken refreshToken, String accessToken) {

        Tokens tokens = Tokens.from(refreshToken, accessToken);
        tokenRepository.save(tokens);
    }

    private static void isExistRefreshToken(String findAccessToken) {

        // 재발급 실패, 400 반환 필요
        if (findAccessToken == null) {
            throw new NoSuchElementException("토큰이 존재하지 않습니다.");
        }
    }
}
