package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    /**
     * 회원 가입 요청 정보를 판매자 엔티티로 변환해 저장한다.
     *
     * @param userRegisterRequest 회원 가입 요청 정보
     * @param userId              연결할 회원의 ID
     */
    public void save(UserRegisterRequest userRegisterRequest, Long userId) {

        sellerRepository.save(userRegisterRequest.toSellerEntity(userId));
    }
}
