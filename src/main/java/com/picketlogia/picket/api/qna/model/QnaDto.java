package com.picketlogia.picket.api.qna.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.user.model.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class QnaDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String contents;
        private Boolean isPrivate;
        private String password;
        private Long productId;

        public Qna toEntity(Long userIdx) {

            Product product = Product.builder()
                    .idx(productId).build();

            User user = User.builder()
                    .idx(userIdx).build();
            return Qna.builder()
                    .title(title)
                    .contents(contents)
                    .isPrivate(isPrivate)
                    .product(product)
                    .user(user)
                    .password(password)
                    .isDeleted(false)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String contents;
        private Boolean isPrivate;
        private String password; // 수정 시 비밀번호 확인용
    }

    @Getter
    public static class Response {
        private final Long idx;
        private final String title;
        private final String contents;
        private final Boolean isPrivate;
        private final List<AnswerResponse> answers;
        @JsonFormat(pattern = "MM.dd(E) HH:mm", timezone = "Asia/Seoul")
        private final Date createdAt;
        private final String prodcutName;
        private final String userNickName;


        public Response(Qna qna) {
            this.idx = qna.getIdx();
            this.title = qna.getTitle();
            this.contents = qna.getIsPrivate() ? "비밀글입니다." : qna.getContents();
            this.isPrivate = qna.getIsPrivate();
            this.createdAt = qna.getCreatedAt();
            this.prodcutName= qna.getProduct().getName();
            this.userNickName=qna.getUser().getNickname();

            // Qna 엔티티에 매핑된 답변 목록을 DTO로 변환 (삭제된 답변은 제외)
            if (qna.getAnswers() != null) {
                this.answers = qna.getAnswers().stream()
                        .filter(answer -> answer.getIsDeleted() != null && !answer.getIsDeleted())
                        .map(AnswerResponse::new)
                        .collect(Collectors.toList());
            } else {
                this.answers = List.of();
            }
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CreateAnswerRequest {
        private String contents;

        public Answer toEntity(Qna qna) {
            return Answer.builder()
                    .contents(contents)
                    .qna(qna)
                    .isDeleted(false)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateAnswerRequest {
        private String contents;
    }

    @Getter
    public static class AnswerResponse {
        private final Long idx;
        private final String contents;
        private final Date createdAt;

        public AnswerResponse(Answer answer) {
            this.idx = answer.getIdx();
            this.contents = answer.getContents();
            this.createdAt = answer.getCreatedAt();
        }
    }
}