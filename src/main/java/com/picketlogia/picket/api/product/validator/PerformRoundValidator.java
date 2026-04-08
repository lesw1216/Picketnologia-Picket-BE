package com.picketlogia.picket.api.product.validator;

import com.picketlogia.picket.api.product.dto.register.PerformanceRoundRegister.ManualRound;
import com.picketlogia.picket.api.product.dto.register.ProductRegisterRequest;
import com.picketlogia.picket.common.exception.BaseException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.picketlogia.picket.common.model.BaseResponseStatus.MANUAL_ROUND_OUT_OF_PERFORMANCE_RANGE;
import static com.picketlogia.picket.common.model.BaseResponseStatus.PERIOD_OUT_OF_PERFORMANCE_RANGE;

@Order(2)
@Component
public class PerformRoundValidator implements BaseProductValidator<ProductRegisterRequest> {

    @Override
    public void validate(ProductRegisterRequest target) {

        validatePeriodWithPerformance(
                target.getStartDate(),
                target.getEndDate(),
                target.getRoundOption().getStartDate(),
                target.getRoundOption().getEndDate()
        );

        validateManualRoundPeriodWithPerformance(
                target.getStartDate(),
                target.getEndDate(),
                target.getRoundOption().getManualRounds()
        );
    }

    /**
     * 회차 일괄 등록 시 회차 기간이 공연 기간 범위에서 벗어났는지 검증한다.
     * @param performanceStart 공연 시작일
     * @param performanceEnd 공연 종료일
     * @param roundStart 회차 시작일
     * @param roundEnd 회차 종료일
     */
    private void validatePeriodWithPerformance(
            LocalDate performanceStart, LocalDate performanceEnd, LocalDate roundStart, LocalDate roundEnd) {

        // 회차 시작일이 공연 시작일 보다 작거나, 공연 종료일 보다 크고
        // 회차 종료일이 공연 시작일 보다 작거나, 공연 종료일 보다 크면 예외 발생
        if (isNotPeriod(performanceStart, performanceEnd, roundStart)
                || isNotPeriod(performanceStart, performanceEnd, roundEnd)) {

            throw BaseException.from(PERIOD_OUT_OF_PERFORMANCE_RANGE);
        }
    }

    /**
     * 수동으로 등록한 회차 리스트가 공연 기간 범위에서 벗어났는지 검증한다.
     * @param performanceStart 공연 시작일
     * @param performanceEnd 공연 종료일
     * @param manualRounds 수동으로 등록한 회차 리스트
     */
    private void validateManualRoundPeriodWithPerformance(
            LocalDate performanceStart, LocalDate performanceEnd, List<ManualRound> manualRounds) {

        for (ManualRound manualRound : manualRounds) {
            if (isNotPeriod(performanceStart, performanceEnd, manualRound.getDate())) {
                throw BaseException.from(MANUAL_ROUND_OUT_OF_PERFORMANCE_RANGE);
            }
        }
    }

    private boolean isNotPeriod(LocalDate start, LocalDate end, LocalDate target) {
        return target.isBefore(start) || target.isAfter(end);
    }
}
