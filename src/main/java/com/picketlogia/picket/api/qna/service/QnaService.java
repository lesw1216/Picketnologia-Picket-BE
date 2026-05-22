package com.picketlogia.picket.api.qna.service;


import com.picketlogia.picket.api.qna.dto.request.QnaAnswerCreateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaAnswerUpdateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaCreateRequest;
import com.picketlogia.picket.api.qna.dto.request.QnaUpdateRequest;
import com.picketlogia.picket.api.qna.dto.response.QnaAnswerResponse;
import com.picketlogia.picket.api.qna.dto.response.QnaResponse;
import com.picketlogia.picket.api.qna.dto.result.QnaPageResult;
import com.picketlogia.picket.api.qna.model.Answer;
import com.picketlogia.picket.api.qna.model.Qna;
import com.picketlogia.picket.api.qna.repository.AnswerRepository;
import com.picketlogia.picket.api.qna.repository.QnaRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final AnswerRepository answerRepository;

    @Transactional(readOnly = true)
    public List<QnaResponse> findAllQna() {
        return qnaRepository.findAllByIsDeletedIsFalse().stream()
                .map(QnaResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QnaResponse findQnaByIdx(Long qnaIdx) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));
        return new QnaResponse(qna);
    }

    @Transactional
    public QnaResponse updateQna(Long qnaIdx, QnaUpdateRequest request) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        if (!Objects.equals(qna.getPassword(), request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        qna.update(request.getTitle(), request.getContents(), request.getIsPrivate());
        return new QnaResponse(qna);
    }

    @Transactional
    public void deleteQna(Long qnaIdx) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));
        qna.delete();
    }

    @Transactional
    public QnaAnswerResponse createAnswer(Long qnaIdx, QnaAnswerCreateRequest request) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        Answer newAnswer = request.toEntity(qna);
        Answer savedAnswer = answerRepository.save(newAnswer);

        return new QnaAnswerResponse(savedAnswer);
    }

    @Transactional
    public QnaAnswerResponse updateAnswer(Long qnaIdx, Long answerIdx, QnaAnswerUpdateRequest request) {
        qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        Answer answer = answerRepository.findByIdxAndIsDeletedIsFalse(answerIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 답변을 찾을 수 없습니다. id=" + answerIdx));

        if (!answer.getQna().getIdx().equals(qnaIdx)) {
            throw new IllegalArgumentException("해당 문의글에 속한 답변이 아닙니다.");
        }

        answer.update(request.getContents());
        return new QnaAnswerResponse(answer);
    }

    @Transactional
    public void deleteAnswer(Long qnaIdx, Long answerIdx) {
        qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        Answer answer = answerRepository.findByIdxAndIsDeletedIsFalse(answerIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 답변을 찾을 수 없습니다. id=" + answerIdx));

        if (!answer.getQna().getIdx().equals(qnaIdx)) {
            throw new IllegalArgumentException("해당 문의글에 속한 답변이 아닙니다.");
        }
        answer.delete();
    }

    public List<QnaResponse> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Qna> result = qnaRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(QnaResponse::new).toList();
    }

    public void save(QnaCreateRequest dto, Long userIdx) {
        qnaRepository.save(dto.toEntity(userIdx));
    }

    public QnaPageResult pnaPaging(Integer page, Integer size, Long productId) {

        Page<Qna> result = qnaRepository.findByProductIdx(productId, PageRequest.of(page, size));

        return QnaPageResult.from(result);
    }
}
