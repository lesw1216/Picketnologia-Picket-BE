package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.token.model.AccessToken;
import com.picketlogia.picket.api.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenRepository tokenRepository;

    /**
     * 로그아웃을 수행합니다.
     * @param removeRefreshToken 삭제할 refresh Token
     */
    public void logout(String removeRefreshToken, String removeAccessToken) {


        String userIdx = AccessToken.getValue(AccessToken.getClaims(removeAccessToken), AccessToken.IDX_NAME);

        boolean deleteResult = tokenRepository.delete(removeRefreshToken);

        if (deleteResult) {
            log.info("[INFO] 로그아웃 성공 - refresh token 삭제 완료, userId={}", userIdx);
        } else {
            log.info("[INFO] 로그아웃 성공 - 삭제할 refresh token 없음, userId={}", userIdx);
        }
    }
}
