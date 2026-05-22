package com.picketlogia.picket.api.qna.dto.result;

import com.picketlogia.picket.api.qna.dto.response.QnaResponse;
import com.picketlogia.picket.api.qna.model.Qna;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class QnaPageResult {
    private List<QnaResponse> responseLists;
    private Integer totalPages;
    private Long totalCount;
    private Integer currentPage;
    private Integer currentSize;

    public static QnaPageResult from(List<Qna> entityList) {
        return QnaPageResult.builder()
                .responseLists(entityList.stream().map(QnaResponse::new).toList())
                .build();
    }

    public static QnaPageResult from(Page<Qna> pageResult) {
        return QnaPageResult.builder()
                .totalPages(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .currentPage(pageResult.getPageable().getPageNumber())
                .currentSize(pageResult.getPageable().getPageSize())
                .responseLists(pageResult.getContent().stream().map(QnaResponse::new).toList())
                .build();
    }
}
