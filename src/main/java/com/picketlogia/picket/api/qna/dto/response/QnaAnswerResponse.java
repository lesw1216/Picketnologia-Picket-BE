package com.picketlogia.picket.api.qna.dto.response;

import com.picketlogia.picket.api.qna.model.Answer;
import lombok.Getter;

import java.util.Date;

@Getter
public class QnaAnswerResponse {
    private final Long idx;
    private final String contents;
    private final Date createdAt;

    public QnaAnswerResponse(Answer answer) {
        this.idx = answer.getIdx();
        this.contents = answer.getContents();
        this.createdAt = answer.getCreatedAt();
    }
}
