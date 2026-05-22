package com.picketlogia.picket.api.qna.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaUpdateRequest {
    private String title;
    private String contents;
    private Boolean isPrivate;
    private String password;
}
