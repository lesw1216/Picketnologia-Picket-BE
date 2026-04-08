package com.picketlogia.picket.api.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordRequest {
    private String token;
    private String email;
    private String password;
}
