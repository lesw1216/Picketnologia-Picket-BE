package com.picketlogia.picket.api.qna.dto.request;

import com.picketlogia.picket.api.qna.model.Answer;
import com.picketlogia.picket.api.qna.model.Qna;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnaAnswerCreateRequest {
    private String contents;

    public Answer toEntity(Qna qna) {
        return Answer.builder()
                .contents(contents)
                .qna(qna)
                .isDeleted(false)
                .build();
    }
}
