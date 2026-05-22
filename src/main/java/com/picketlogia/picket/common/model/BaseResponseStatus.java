package com.picketlogia.picket.common.model;


import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 20000 : 요청 성공
     */
    SUCCESS(true, 20000, "요청에 성공하였습니다."),


    /**
     * 30000 : Request 오류, Validation 오류
     */
    // Common
    INVALID_JWT(false, 20002, "유효하지 않은 JWT입니다."),
    INVALID_USER_ROLE(false,20003,"권한이 없는 유저의 접근입니다."),
    INVALID_USER_INFO(false,20004,"이메일 또는 비밀번호를 확인해주세요."),
    INVALID_USER_DISABLED(false,20005,"이메일 인증이 필요합니다. 이메일을 확인해주세요."),
    DUPLICATE_USER_EMAIL(false,20006,"중복된 이메일입니다. 다른 이메일을 사용해주세요."),
    LECTURE_NOT_IN_COURSE(false,20007,"해당 강의는 해당 코스의 강의가 아닙니다."),
    ALREADY_LECTURE_COMPLETE(false,20008,"해당 강의를 이미 완료하였습니다."),
    INVALID_USER_PASSWORD(false,20009,"비밀번호가 일치하지 않습니다."),
    INVALID_USER_EMAIL(false,20010,"이메일 정보가 잘못되었습니다."),
    INVALID_EMAIL_RESET_TIMEOUT(false,20011,"이메일 변경 링크가 만료되었습니다. 다시 시도해주세요."),

    GLOBAL_EXCEPTION(false, 30000, "요청을 처리하는 과정에서 문제가 발생하였습니다."),
    REQUEST_ERROR(false, 30001, "입력값을 확인해주세요."),
    INVALID_AUTH_CODE(false, 30002, "유효하지 않은 인증번호 입니다."),
    INVALID_REGION_CODE(false, 30003, "유효하지 않은 지역 코드 입니다"),
    INVALID_ROUND_DAY(false, 30004, "유효하지 않은 회차 요일입니다."),
    PERIOD_OUT_OF_PERFORMANCE_RANGE(false, 30005, "회차 등록 기간이 공연 기간 범위가 아닙니다."),
    MANUAL_ROUND_OUT_OF_PERFORMANCE_RANGE(false, 30006, "회차 날짜가 공연 기간 범위가 아닙니다."),
    OPEN_DATE_OUT_OF_PERFORMANCE_RANGE(false, 30007, "오픈 예정일은 공연 시작일 보다 빨라야 합니다."),
    EXPIRED_JWT(false, 30008, "JWT 토큰이 만료되었습니다."),

    // 결제, 주문
    SEAT_ALREADY_BOOKED(false, 31000, "이미 예약된 좌석입니다, 결제가 실패되었습니다."),
    FAILED_PAYMENT_WEBHOOK(false, 31001, "결제 관련 웹훅 에러가 발생했습니다."),
    ERROR_PAYMENT_STATUS_PAID(false, 31002, "PAID 상태 처리에서 에러가 발생했습니다."),
    NOT_ROCKED_SEAT(false, 31003, "좌석 선택 유효기간이 지났습니다."),

    NOT_FOUND_DATA(false, 32000, "해당 데이터를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(false, 32001, "존재하지 않는 공연입니다."),
    GENRE_NOT_FOUND(false, 32002, "존재하지 않는 장르입니다."),
    QNA_NOT_FOUND(false, 32003, "존재하지 않는 문의글입니다."),
    QNA_ANSWER_NOT_FOUND(false, 32004, "존재하지 않는 답변입니다."),
    QNA_PASSWORD_MISMATCH(false, 32005, "문의글 비밀번호가 일치하지 않습니다."),
    QNA_ANSWER_MISMATCH(false, 32006, "해당 문의글에 속한 답변이 아닙니다."),
    PAYMENT_DATA_INVALID(false, 32007, "결제 데이터 처리 중 오류가 발생했습니다."),
    /**
     * 40000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 40001, "값을 불러오는데 실패하였습니다."),
    RESPONSE_NULL_ERROR(false,40002,"[NULL]입력된 IDX 값로 접근한 DB의 유효한 ROW가 존재하지 않습니다."),

    ORDERS_VALIDATION_FAIL(false, 40003, "결제 정보가 잘못되었습니다."),
    IAMPORT_ERROR(false, 40004, "결제 금액이 잘못되었습니다."),
    ORDERS_NOT_ORDERED(false, 40005, "결제 정보가 없습니다. 구매 후 이용해주세요."),

    /**
     * 50000 : Database 오류
     */
    DATABASE_ERROR(false, 50001, "데이터베이스 연결에 실패하였습니다."),

    /**
     * 60000 : Server 오류
     */
    SERVER_ERROR(false, 60001, "서버와의 연결에 실패하였습니다.");



    /**
     * 70000 : 커스텀
     */



    // 70000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
