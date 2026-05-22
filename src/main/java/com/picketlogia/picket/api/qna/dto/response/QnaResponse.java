package com.picketlogia.picket.api.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picketlogia.picket.api.qna.model.Qna;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QnaResponse {
    private final Long idx;
    private final String title;
    private final String contents;
    private final Boolean isPrivate;
    private final List<QnaAnswerResponse> answers;
    @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
    private final Date createdAt;
    private final String prodcutName;
    private final String userNickName;


    public QnaResponse(Qna qna) {
        this.idx = qna.getIdx();
        this.title = qna.getTitle();
        this.contents = qna.getIsPrivate() ? "비밀글입니다." : qna.getContents();
        this.isPrivate = qna.getIsPrivate();
        this.createdAt = qna.getCreatedAt();
        this.prodcutName = qna.getProduct().getName();
        this.userNickName = qna.getUser().getNickname();

        if (qna.getAnswers() != null) {
            this.answers = qna.getAnswers().stream()
                    .filter(answer -> answer.getIsDeleted() != null && !answer.getIsDeleted())
                    .map(QnaAnswerResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.answers = List.of();
        }
    }
}
