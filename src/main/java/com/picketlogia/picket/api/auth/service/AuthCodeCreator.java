package com.picketlogia.picket.api.auth.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AuthCodeCreator {

    private static final int NUMBER_BOUND = 1000000;

    /**
     * 6자리 숫자 인증 번호를 생성한다.
     *
     * @return 0으로 zero-padding된 6자리 문자열 인증 코드
     */
    public String generateAuthCode() {

        SecureRandom secureRandom = new SecureRandom();
        int randomCode = secureRandom.nextInt(NUMBER_BOUND);

        return String.format("%06d", randomCode);
    }
}
