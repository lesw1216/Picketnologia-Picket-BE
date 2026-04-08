package com.picketlogia.picket.api.auth.dto.request;

import lombok.Getter;

@Getter
public class AuthCodeMailRequest {
    private String code;
    private String email;
}
