package com.picketlogia.picket.api.qna.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class QnaList {
    private List<QnaDto.Response> ResponseLists;
    private Integer totalPages;
    private Long totalCount;
    private Integer currentPage;
    private Integer currentSize;

    public static  QnaList from(List<Qna> entityList) {
        return QnaList.builder()
                .ResponseLists(entityList.stream().map(QnaDto.Response::new).toList())
                .build();
    }

    public static QnaList from(Page<Qna> pageResult) {
        return QnaList.builder()
                .totalPages(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .currentPage(pageResult.getPageable().getPageNumber())
                .currentSize(pageResult.getPageable().getPageSize())
                .ResponseLists(pageResult.getContent().stream().map(QnaDto.Response::new).toList())
                .build();
    }
}