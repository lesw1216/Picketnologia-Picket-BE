package com.picketlogia.picket.api.qna.service;


import com.picketlogia.picket.api.qna.model.Answer;
import com.picketlogia.picket.api.qna.model.QnaDto;
import com.picketlogia.picket.api.qna.model.Qna;
import com.picketlogia.picket.api.qna.model.QnaList;
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

//    @Transactional
//    public QnaDto.Response createQna(QnaDto.CreateRequest request) {
//        Qna newQna = request.toEntity();
//        Qna savedQna = qnaRepository.save(newQna);
//        return new QnaDto.Response(savedQna);
//    }

    @Transactional(readOnly = true)
    public List<QnaDto.Response> findAllQna() {
        return qnaRepository.findAllByIsDeletedIsFalse().stream()
                .map(QnaDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QnaDto.Response findQnaByIdx(Long qnaIdx) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));
        return new QnaDto.Response(qna);
    }

    @Transactional
    public QnaDto.Response updateQna(Long qnaIdx, QnaDto.UpdateRequest request) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        if (!Objects.equals(qna.getPassword(), request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        qna.update(request.getTitle(), request.getContents(), request.getIsPrivate());
        return new QnaDto.Response(qna);
    }

    @Transactional
    public void deleteQna(Long qnaIdx) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));
        qna.delete();
    }

    @Transactional
    public QnaDto.AnswerResponse createAnswer(Long qnaIdx, QnaDto.CreateAnswerRequest request) {
        Qna qna = qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        Answer newAnswer = request.toEntity(qna);
        Answer savedAnswer = answerRepository.save(newAnswer);

        return new QnaDto.AnswerResponse(savedAnswer);
    }

    @Transactional
    public QnaDto.AnswerResponse updateAnswer(Long qnaIdx, Long answerIdx, QnaDto.UpdateAnswerRequest request) {
        // 1. 문의글이 존재하는지 확인
        qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        // 2. 답변이 존재하는지 확인
        Answer answer = answerRepository.findByIdxAndIsDeletedIsFalse(answerIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 답변을 찾을 수 없습니다. id=" + answerIdx));

        // 3. 답변이 해당 문의글에 속해 있는지 검증
        if (!answer.getQna().getIdx().equals(qnaIdx)) {
            throw new IllegalArgumentException("해당 문의글에 속한 답변이 아닙니다.");
        }

        answer.update(request.getContents());
        return new QnaDto.AnswerResponse(answer);
    }

    @Transactional
    public void deleteAnswer(Long qnaIdx, Long answerIdx) {
        // 1. 문의글이 존재하는지 확인
        qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글을 찾을 수 없습니다. id=" + qnaIdx));

        // 2. 답변이 존재하는지 확인
        Answer answer = answerRepository.findByIdxAndIsDeletedIsFalse(answerIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 답변을 찾을 수 없습니다. id=" + answerIdx));

        // 3. 답변이 해당 문의글에 속해 있는지 검증
        if (!answer.getQna().getIdx().equals(qnaIdx)) {
            throw new IllegalArgumentException("해당 문의글에 속한 답변이 아닙니다.");
        }
        answer.delete();
    }

    public List<QnaDto.Response> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Qna> result = qnaRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(QnaDto.Response::new).toList();
    }

    public void save(QnaDto.CreateRequest dto, Long userIdx) {
        qnaRepository.save(dto.toEntity(userIdx));

    }

    public QnaList pnaPaging(Integer page, Integer size , Long productId) {

        Page<Qna> result = qnaRepository.findByProductIdx(productId, PageRequest.of(page,size)); // 페이지네이션이 필요하면 사용

        return QnaList.from(result);
    }


}
