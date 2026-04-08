package com.picketlogia.picket.api.user.service;

import com.picketlogia.picket.api.user.dto.request.UserRegisterRequest;
import com.picketlogia.picket.api.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public void save(UserRegisterRequest userRegisterRequest, Long userId) {
        sellerRepository.save(userRegisterRequest.toSellerEntity(userId));
    }
}
