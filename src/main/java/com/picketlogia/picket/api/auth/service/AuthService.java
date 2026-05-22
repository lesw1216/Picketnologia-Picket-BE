package com.picketlogia.picket.api.auth.service;

import com.picketlogia.picket.api.auth.dto.request.AuthCodeMailRequest;
import com.picketlogia.picket.api.auth.dto.request.ResetPasswordRequest;
import com.picketlogia.picket.api.auth.model.MailSend;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 이메일과 인증 코드를 검증하고, 일치하면 Redis에 저장된 코드를 즉시 만료시킨다.
     *
     * @param authCodeMail 이메일과 인증 코드가 담긴 요청 객체
     * @throws BaseException 인증 코드가 만료되었거나 일치하지 않을 때
     */
    public void verifyAuthCode(AuthCodeMailRequest authCodeMail) {

        String redisKey = MailSend.AUTH_CODE_MAIL.createRedisKey(authCodeMail.getEmail());
        String findAuthCode = getValue(redisKey);

        if (findAuthCode == null || !findAuthCode.equals(authCodeMail.getCode())) {
            throw BaseException.from(BaseResponseStatus.INVALID_AUTH_CODE);
        }

        dateDeleteByRedisKey(redisKey);
    }

    /**
     * 비밀번호 재설정 토큰이 유효한지 확인하고, 매핑된 이메일을 반환한다.
     *
     * @param uuid 비밀번호 재설정 링크에 포함된 일회용 토큰
     * @return 토큰에 연결된 이메일이 담긴 요청 객체
     * @throws BaseException 토큰이 만료되었거나 존재하지 않을 때
     */
    public ResetPasswordRequest verifyUserPasswordReset(String uuid) {

        String redisKey = MailSend.PASSWORD_RESET_MAIL.createRedisKey(uuid);
        String findEmail = getValue(redisKey);

        if (findEmail == null) {
            throw BaseException.from(BaseResponseStatus.INVALID_EMAIL_RESET_TIMEOUT);
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
