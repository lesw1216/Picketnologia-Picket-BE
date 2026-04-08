package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.dto.request.PerformanceRoundRequest;
import com.picketlogia.picket.api.product.dto.request.PerformanceRoundRequest.SelectedDay;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.model.RoundDate;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.api.product.repository.RoundDateRepository;
import com.picketlogia.picket.api.product.repository.RoundTimeRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.picketlogia.picket.api.product.dto.request.PerformanceRoundRequest.ManualRound;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceRoundService {

    private final RoundDateRepository roundDateRepository;
    private final RoundTimeRepository roundTimeRepository;

    /**
     * <code>currentDay</code>의 회차 시간 리스트를 가져온다. <br>
     *
     * @param selectedDays 사용자가 선택한 날짜와 날짜에 맞는 회차 시간 리스트가 담긴 객체의 리스트
     * @param currentDay   현재 날짜의 요일
     * @return <code>List<<code>LocalTime</code>></code> 회차 시간 리스트
     * @throws BaseException 유효하지 않은 요일을 요청한 경우 예외 발생
     */
    private static List<LocalTime> getRoundTimesForDay(List<SelectedDay> selectedDays, DayOfWeek currentDay) {
        SelectedDay findDay = selectedDays.stream()
                .filter(
                        selectedDay -> currentDay.equals(DayOfWeek.valueOf(selectedDay.getCode()))
                ).findFirst().orElseThrow(
                        () -> BaseException.from(BaseResponseStatus.INVALID_ROUND_DAY)
                );

        return findDay.getTimes();
    }

    /**
     * 회차 등록
     *
     * @param roundOption 회차 등록 옵션
     * @param product     회차에 맞는 상품
     */
    public void register(PerformanceRoundRequest roundOption, Product product) {

        LocalDate date = roundOption.getStartDate();

        // 날짜가 마지막 날짜를 넘지 않을 때 까지 반복
        while (!date.isAfter(roundOption.getEndDate())) {

            // 특정 기간 회차 일괄 생성
            registerRoundsForPeriod(roundOption, date, product);

            // 1일 증가한다.
            date = date.plusDays(1);
        }

        // 수동으로 날짜와 요일을 등록한 경우
        registerManualRounds(roundOption.getManualRounds(), product);
    }

    /**
     * 특정 기간 동안의 회차 일괄 등록
     *
     * @param roundOption 회차 등록 옵션
     * @param date        특정 기간 내의 날짜
     * @param product     회차에 맞는 상품
     */
    private void registerRoundsForPeriod(PerformanceRoundRequest roundOption, LocalDate date, Product product) {
        DayOfWeek currentDay = date.getDayOfWeek();

        // 등록 요일이 담긴 객체 배열에서 현재 날짜의 요일이 있는지 체크
        boolean hasSelected = matchesCurrentDay(roundOption.getSelectedDays(), currentDay);

        // 현재 날짜의 요일이 회차 등록 요일이라면
        if (hasSelected) {

            // 해당 요일의 회차 시간을 가져온다.
            List<LocalTime> roundTimes = getRoundTimes(roundOption, currentDay);

            for (LocalTime roundTime : roundTimes) {
                // 실제 DB에 저장
                saveTo(date, roundTime, product);
            }

        }
    }

    /**
     * <code>currentDay</code>의 회차 시간 리스트를 가져온다. <br>
     * 이 때, 모든 요일의 회차 시간이 동일한 경우와, 요일 마다 회차 시간이 다른 경우를 나누어 가져온다.
     *
     * @param roundOption 회차 등록 옵션
     * @param currentDay  현재 날짜의 요일
     * @return <code>List<<code>LocalTime</code>></code> 회차 시간 리스트
     */
    private List<LocalTime> getRoundTimes(PerformanceRoundRequest roundOption, DayOfWeek currentDay) {

        // 요일별로 시간이 다르면
        boolean isNotSameTime = roundOption.getSameTimes().isEmpty();
        if (isNotSameTime) {
            return getRoundTimesForDay(roundOption.getSelectedDays(), currentDay);
        }

        // 모든 요일의 회차 시간이 똑같다면
        return roundOption.getSameTimes();
    }

    /**
     * 회차를 등록하기로한 요일인지 검증한다. <br>
     * (예시: 사용자가 월요일과 화요일에 회차를 등록하기로 했을 경우, <code>currentDay</code>이 그외의 요일이라면 <code>false</code>를 반환한다.
     *
     * @param selectedDays 사용자가 선택한 날짜와 날짜에 맞는 회차 시간 리스트가 담긴 객체의 리스트
     * @param currentDay   요일 검증을 위한 현재 날짜
     * @return 사용자가 회차 등록으로 선택한 요일과 같다면 <code>true</code>를 반환한다. 그렇지 않다면 <code>false</code>를 반환한다.
     */
    private boolean matchesCurrentDay(List<SelectedDay> selectedDays, DayOfWeek currentDay) {
        return selectedDays.stream().anyMatch(
                selectedDay -> currentDay.equals(DayOfWeek.valueOf(selectedDay.getCode()))
        );
    }

    /**
     * 수동으로 날짜와 요일을 등록한 경우
     *
     * @param manualRounds 수동으로 등록한 회차 리스트
     * @param product      회차에 맞는 상품
     */
    private void registerManualRounds(List<ManualRound> manualRounds, Product product) {
        for (ManualRound manualRound : manualRounds) {
            saveTo(manualRound.getDate(), manualRound.getTime(), product);
        }
    }

    /**
     * DB에 저장한다.
     *
     * @param roundDate 회차 날짜
     * @param roundTime 회차 시간
     * @param product   회차에 맞는 상품
     */
    private void saveTo(LocalDate roundDate, LocalTime roundTime, Product product) {

        // 회차 날짜 저장
        RoundDate savedRoundDate = roundDateRepository.save(RoundDate.builder()
                .date(roundDate)
                .product(product)
                .build());

        // 회차 시간 저장
        roundTimeRepository.save(
                RoundTime.builder()
                        .roundDate(savedRoundDate)
                        .time(roundTime)
                        .build()
        );
    }
}
