package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserAuthRequest;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> findUser = userRepository.findByEmail(username);
        User user = findUser.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

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
