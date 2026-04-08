package com.picketlogia.picket.api.auth.service;

import com.picketlogia.picket.api.auth.dto.request.AuthCodeMailRequest;
import com.picketlogia.picket.api.auth.dto.request.ResetPasswordRequest;
import com.picketlogia.picket.api.auth.model.MailSend;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 인증 번호를 검증한다.
     * @param authCodeMail 이메일과 인증 코드가 담긴 dto
     * @throws IllegalArgumentException 유효하지 않은 인증 번호인 경우 예외 발생
     */
    public void verifyAuthCode(AuthCodeMailRequest authCodeMail) {

        String redisKey = MailSend.AUTH_CODE_MAIL.createRedisKey(authCodeMail.getEmail());
        String findAuthCode = getValue(redisKey);

        if (findAuthCode == null || !findAuthCode.equals(authCodeMail.getCode())) {
            throw new IllegalArgumentException("유효하지 않는 인증 번호");
        }

        dateDeleteByRedisKey(redisKey);
    }

    /**
     * 비밀번호를 재설정 하기 전 올바른 인증 토큰인지 확인.
     * @param uuid 인증이 필요한 토큰
     * @return <code>ResetPasswordDto</code>
     * @throws IllegalArgumentException 유효하지 않은 인증 토큰인 경우 예외 발생
     */
    public ResetPasswordRequest verifyUserPasswordReset(String uuid) {

        String redisKey = MailSend.PASSWORD_RESET_MAIL.createRedisKey(uuid);
        String findEmail = getValue(redisKey);

        if (findEmail == null) {
            throw new IllegalArgumentException("유효하지 않는 인증 토큰");
        }

        dateDeleteByRedisKey(redisKey);
        return ResetPasswordRequest.builder()
                .email(findEmail)
                .build();
    }

    private String getValue(String redisKey) {
        return stringRedisTemplate.opsForValue().get(redisKey);
    }

    private void dateDeleteByRedisKey(String redisKey) {
        stringRedisTemplate.delete(redisKey);
    }
}
