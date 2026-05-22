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
     * refresh token을 key, access token을 value로 Redis에 저장한다.
     *
     * @param tokens 저장할 refresh·access token 쌍
     */
    public void save(TokensResponse tokens) {

        RefreshToken refreshToken = tokens.getRefreshToken();
        String refreshTokenValue = refreshToken.getValue();
        Long refreshTokenExpire = refreshToken.getExpireMillis();

        String accessToken = tokens.getAccessToken();

        redisTemplate.opsForValue().set(refreshTokenValue, accessToken, Duration.ofMillis(refreshTokenExpire));
    }

    /**
     * refresh token에 매핑된 access token을 조회한다.
     *
     * @param refreshToken 조회 키로 사용할 refresh token
     * @return 매핑된 access token 문자열, 존재하지 않으면 {@code null}
     */
    public String findAccessToken(String refreshToken) {

        return redisTemplate.opsForValue().get(refreshToken);
    }

    /**
     * Lua script로 refresh token rotation을 원자적으로 수행한다.
     *
     * @param previousRefreshToken     기존 refresh token
     * @param requestAccessToken       요청으로 받은 access token
     * @param reissuedRefreshToken     새로 발급한 refresh token
     * @param reissuedAccessToken      새로 발급한 access token
     * @param refreshTokenExpireMillis 새 refresh token 만료 시간(ms)
     * @return 1: 재발급 성공, 0: refresh token 없음, -1: access token 불일치
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
     * Redis에서 refresh token 키를 제거한다.
     *
     * @param refreshToken 삭제할 refresh token
     * @return 키가 실제로 삭제됐으면 {@code true}, 없었으면 {@code false}
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
