package com.picketlogia.picket.api.seller.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @GetMapping("/test")
    public String test() {
        return "seller 권한입니다.";
    }
}
