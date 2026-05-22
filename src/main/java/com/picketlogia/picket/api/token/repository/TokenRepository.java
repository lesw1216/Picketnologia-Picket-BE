package com.picketlogia.picket.api.token.repository;

import com.picketlogia.picket.api.token.dto.response.TokensResponse;
import com.picketlogia.picket.api.token.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> reissueTokensScript = createReissueTokensScript();

    /**
     * refresh Token과 Access Token을 저장합니다. <br>
     * Key 값은 refresh token이 저장됩니다. <br>
     * Value 값은 access token이 저장됩니다. <br>
     * @param tokens 저장할 refresh Token과 Access Token을 담은 <code>TokensResponse</code> 객체
     */
    public void save(TokensResponse tokens) {

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
    public String findAccessToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    /**
     * refresh token rotation을 Lua script로 원자적으로 수행합니다.
     * @param previousRefreshToken 기존 refresh Token
     * @param requestAccessToken 요청으로 받은 Access Token
     * @param reissuedRefreshToken 새로 발급한 refresh Token
     * @param reissuedAccessToken 새로 발급한 Access Token
     * @param refreshTokenExpireMillis 새 refresh Token 만료 시간(ms)
     * @return <code>1</code> - 재발급 성공, <code>0</code> - refresh Token 없음, <code>-1</code> - access Token 불일치
     */
    public long reissueTokensAtomically(String previousRefreshToken,
                                        String requestAccessToken,
                                        RefreshToken reissuedRefreshToken,
                                        String reissuedAccessToken,
                                        Long refreshTokenExpireMillis) {

        return redisTemplate.execute(
                reissueTokensScript,
                List.of(previousRefreshToken, reissuedRefreshToken.getValue()),
                requestAccessToken,
                reissuedAccessToken,
                String.valueOf(refreshTokenExpireMillis)
        );
    }

    /**
     * refresh Token을 삭제합니다.
     * @param refreshToken 삭제할 refresh Token
     * @return <code>true</code> - 성공, <code>false</code> - 실패
     */
    public boolean delete(String refreshToken) {
        return redisTemplate.delete(refreshToken);
    }

    private RedisScript<Long> createReissueTokensScript() {

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("scripts/reissue-tokens.lua"));
        redisScript.setResultType(Long.class);

        return redisScript;
    }
}
