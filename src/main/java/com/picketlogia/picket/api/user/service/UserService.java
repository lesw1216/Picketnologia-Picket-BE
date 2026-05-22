package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    /**
     * 회원 가입 요청을 User 엔티티로 변환하고 비밀번호를 암호화해 저장한다.
     *
     * @param register 회원 가입 요청 정보
     * @return 저장된 User 엔티티
     */
    public User signup(UserRegisterRequest register) {

        User userEntity = register.toUserEntity();

        passwordService.encode(userEntity, register.getPassword());

        return userRepository.save(userEntity);
    }
}
