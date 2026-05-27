package com.picketlogia.picket.api.seller.controller;

import com.picketlogia.picket.common.model.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    /**
     * 호출한 사용자가 판매자 권한을 보유하고 있는지 확인하는 헬스체크 엔드포인트.
     *
     * @return 판매자 권한 보유 메시지를 담은 표준 응답
     */
    @GetMapping("/test")
    public ResponseEntity<BaseResponse<String>> test() {

        return ResponseEntity.ok(BaseResponse.success("seller 권한입니다."));
    }
}
