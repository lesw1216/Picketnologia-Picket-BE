package com.picketlogia.picket.api.payments.dto.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
@ToString
public class PaymentCustomDataCommand {

    private Long productIdx;
    private Long roundTimeIdx;
    private List<Long> seatIdxes;

    /**
     * PortOne 응답의 customData JSON 문자열을 Command 객체로 역직렬화한다.
     *
     * @param customData JSON 형식의 customData 문자열
     * @return 역직렬화된 Command
     * @throws BaseException JSON 파싱에 실패했을 때
     */
    public static PaymentCustomDataCommand from(String customData) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(customData, PaymentCustomDataCommand.class);
        } catch (JsonProcessingException e) {
            log.error("결제 customData 파싱 실패", e);
            throw BaseException.from(BaseResponseStatus.PAYMENT_DATA_INVALID);
        }
    }
}
