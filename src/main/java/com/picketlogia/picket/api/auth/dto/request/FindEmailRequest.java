package com.picketlogia.picket.api.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindEmailRequest {
    private String name;
    private String phoneNumber;
}
