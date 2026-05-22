package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    /**
     * 평문 비밀번호를 암호화해 User 엔티티에 반영한다.
     *
     * @param entity   비밀번호를 갱신할 User 엔티티
     * @param password 평문 비밀번호
     */
    public void encode(User entity, String password) {

        String encodedPassword = passwordEncoder.encode(password);

        entity.updatePassword(encodedPassword);
    }

    /**
     * 비밀번호 변경 의미를 가지며 내부적으로 새 비밀번호를 암호화해 적용한다.
     *
     * @param entity   비밀번호를 변경할 User 엔티티
     * @param password 새 평문 비밀번호
     */
    public void changePassword(User entity, String password) {

        encode(entity, password);
    }
}
