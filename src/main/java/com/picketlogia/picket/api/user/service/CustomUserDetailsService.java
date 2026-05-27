package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 이메일을 username으로 사용해 회원을 조회하고 Spring Security용 UserDetails로 변환한다.
     *
     * @param username 조회 기준이 되는 이메일 주소
     * @return 인증 컨텍스트에 사용할 UserDetails
     * @throws UsernameNotFoundException Spring Security 계약상 회원이 없을 때 (BaseException 사용 예외)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return UserAuthRequest.builder()
                .idx(user.getIdx())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .role(user.getUserRole().getName())
                .userType(user.getUserType().name())
                .build();
    }
}
