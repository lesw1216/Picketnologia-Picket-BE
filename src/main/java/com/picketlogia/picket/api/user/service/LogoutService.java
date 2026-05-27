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
     * Redis에 저장된 refresh token을 제거하고 결과를 로깅한다.
     *
     * @param removeRefreshToken 삭제할 refresh token 값
     * @param removeAccessToken  사용자 식별자 추출용 access token 값
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
