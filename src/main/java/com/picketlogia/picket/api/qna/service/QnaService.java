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

    /**
     * 삭제되지 않은 문의글을 전부 조회한다.
     *
     * @return 조회된 문의글 응답 목록
     */
    @Transactional(readOnly = true)
    public List<QnaResponse> findAllQna() {

        return qnaRepository.findAllByIsDeletedIsFalse().stream()
                .map(QnaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * ID로 단일 문의글을 조회한다.
     *
     * @param qnaIdx 조회할 문의글 ID
     * @return 문의글 응답
     * @throws BaseException 문의글이 존재하지 않을 때
     */
    @Transactional(readOnly = true)
    public QnaResponse findQnaByIdx(Long qnaIdx) {

        Qna qna = findQnaOrThrow(qnaIdx);

        return new QnaResponse(qna);
    }

    /**
     * 비밀번호 검증 후 문의글 본문을 수정한다.
     *
     * @param qnaIdx  수정할 문의글 ID
     * @param request 수정 요청 정보
     * @return 수정된 문의글 응답
     * @throws BaseException 문의글이 없거나 비밀번호가 일치하지 않을 때
     */
    @Transactional
    public QnaResponse updateQna(Long qnaIdx, QnaUpdateRequest request) {

        Qna qna = findQnaOrThrow(qnaIdx);

        if (!Objects.equals(qna.getPassword(), request.getPassword())) {
            throw BaseException.from(BaseResponseStatus.QNA_PASSWORD_MISMATCH);
        }

        qna.update(request.getTitle(), request.getContents(), request.getIsPrivate());

        return new QnaResponse(qna);
    }

    /**
     * 문의글을 소프트 삭제한다.
     *
     * @param qnaIdx 삭제할 문의글 ID
     * @throws BaseException 문의글이 존재하지 않을 때
     */
    @Transactional
    public void deleteQna(Long qnaIdx) {

        Qna qna = findQnaOrThrow(qnaIdx);
        qna.delete();
    }

    /**
     * 문의글에 답변을 새로 등록한다.
     *
     * @param qnaIdx  답변을 달 문의글 ID
     * @param request 답변 등록 요청
     * @return 등록된 답변 응답
     * @throws BaseException 문의글이 존재하지 않을 때
     */
    @Transactional
    public QnaAnswerResponse createAnswer(Long qnaIdx, QnaAnswerCreateRequest request) {

        Qna qna = findQnaOrThrow(qnaIdx);

        Answer newAnswer = request.toEntity(qna);
        Answer savedAnswer = answerRepository.save(newAnswer);

        return new QnaAnswerResponse(savedAnswer);
    }

    /**
     * 문의글에 속한 답변 본문을 수정한다.
     *
     * @param qnaIdx    답변이 속한 문의글 ID
     * @param answerIdx 수정할 답변 ID
     * @param request   답변 수정 요청
     * @return 수정된 답변 응답
     * @throws BaseException 문의글·답변이 없거나 소속 관계가 어긋날 때
     */
    @Transactional
    public QnaAnswerResponse updateAnswer(Long qnaIdx, Long answerIdx, QnaAnswerUpdateRequest request) {

        findQnaOrThrow(qnaIdx);
        Answer answer = findAnswerOrThrow(answerIdx);
        validateAnswerBelongsToQna(answer, qnaIdx);

        answer.update(request.getContents());

        return new QnaAnswerResponse(answer);
    }

    /**
     * 문의글에 속한 답변을 소프트 삭제한다.
     *
     * @param qnaIdx    답변이 속한 문의글 ID
     * @param answerIdx 삭제할 답변 ID
     * @throws BaseException 문의글·답변이 없거나 소속 관계가 어긋날 때
     */
    @Transactional
    public void deleteAnswer(Long qnaIdx, Long answerIdx) {

        findQnaOrThrow(qnaIdx);
        Answer answer = findAnswerOrThrow(answerIdx);
        validateAnswerBelongsToQna(answer, qnaIdx);

        answer.delete();
    }

    /**
     * 사용자가 지정 기간 동안 작성한 문의글을 조회한다.
     *
     * @param userIdx      조회 대상 사용자 ID
     * @param startDateStr 시작일 (yyyy-MM-dd)
     * @param endDateStr   종료일 (yyyy-MM-dd)
     * @return 기간 내 문의글 응답 목록
     */
    public List<QnaResponse> findAllByUserIdxAndCreatedAtBetween(Long userIdx, String startDateStr, String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Qna> result = qnaRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(QnaResponse::new).toList();
    }

    /**
     * 문의글을 신규 저장한다.
     *
     * @param dto     문의글 등록 요청
     * @param userIdx 작성자 ID
     */
    public void save(QnaCreateRequest dto, Long userIdx) {

        qnaRepository.save(dto.toEntity(userIdx));
    }

    /**
     * 상품별 문의글 페이지 결과를 조회한다.
     *
     * @param page      페이지 번호 (0부터)
     * @param size      페이지 크기
     * @param productId 조회 대상 상품 ID
     * @return 페이징 결과
     */
    public QnaPageResult findAllPagedByProductIdx(Integer page, Integer size, Long productId) {

        Page<Qna> result = qnaRepository.findByProductIdx(productId, PageRequest.of(page, size));

        return QnaPageResult.from(result);
    }

    private Qna findQnaOrThrow(Long qnaIdx) {

        return qnaRepository.findByIdxAndIsDeletedIsFalse(qnaIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.QNA_NOT_FOUND));
    }

    private Answer findAnswerOrThrow(Long answerIdx) {

        return answerRepository.findByIdxAndIsDeletedIsFalse(answerIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.QNA_ANSWER_NOT_FOUND));
    }

    private void validateAnswerBelongsToQna(Answer answer, Long qnaIdx) {

        if (!answer.getQna().getIdx().equals(qnaIdx)) {
            throw BaseException.from(BaseResponseStatus.QNA_ANSWER_MISMATCH);
        }
    }
}
