package com.picketlogia.picket.api.token.repository;

import com.picketlogia.picket.api.token.dto.Tokens;
import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * refresh Token과 Access Token을 저장합니다. <br>
     * Key 값은 refresh token이 저장됩니다. <br>
     * Value 값은 access token이 저장됩니다. <br>
     * @param tokens 저장할 refresh Token과 Access Token을 담은 <code>Tokens</code> 객체
     */
    public void save(Tokens tokens) {

        // find refresh token.
        RefreshToken refreshToken = tokens.getRefreshToken();
        String refreshTokenValue = refreshToken.getValue();
        Long refreshTokenExpire = refreshToken.getExpireMillis();

        // find access token.
        String accessToken = tokens.getAccessToken();

        redisTemplate.opsForValue().set(refreshTokenValue, accessToken, Duration.ofMillis(refreshTokenExpire));
    }

    /**
     * refresh Token을 조회합니다.
     * @param refreshToken 조회할 refresh Token
     * @return <code>String</code> 타입의 Access Token, refresh Token이 존재하지 않으면 <code>null</code>을 반환합니다.
     */
    public String findTokens(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    /**
     * refresh Token을 삭제합니다.
     * @param refreshToken 삭제할 refresh Token
     * @return <code>true</code> - 성공, <code>false</code> - 실패
     */
    public boolean delete(String refreshToken) {
        return redisTemplate.delete(refreshToken);
    }
}
