package com.picketlogia.picket.api.product.service;

import com.picketlogia.picket.api.product.dto.RoundDatesResult;
import com.picketlogia.picket.api.product.dto.RoundTimesResult;
import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.product.model.entity.RoundDate;
import com.picketlogia.picket.api.product.model.entity.RoundTime;
import com.picketlogia.picket.api.product.repository.RoundDateRepository;
import com.picketlogia.picket.api.product.repository.RoundTimeRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundDateRepository roundDateRepository;
    private final RoundTimeRepository roundTimeRepository;

    /**
     * 특정 공연의 회차일을 조회한다. 이때, 오늘 이전의 회차일은 제외한다.
     *
     * @param productIdx 공연 idx
     * @return 공연일 리스트
     */
    public RoundDatesResult findAllByProductIdx(Long productIdx) {

        List<RoundDate> roundDates = roundDateRepository.findALlByProductAndDateGreaterThanEqualOrderByDate(
                Product.builder().idx(productIdx).build(),
                LocalDate.now()
        );

        return RoundDatesResult.from(roundDates);
    }

    /**
     * 선택한 날짜의 회차시간을 조회한다. 이때, 날짜가 오늘이라면 현재 시간 이후의 회차 시간을 조회한다.
     *
     * @param roundDateIdx 회차일 idx
     * @return 회차시간 리스트
     */
    public RoundTimesResult findRoundTimesByRoundDate(Long roundDateIdx) {

        List<RoundTime> roundTimes;

        RoundDate roundDate = roundDateRepository.findById(roundDateIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.INVALID_ROUND_DAY));

        if (roundDate.getDate().isEqual(LocalDate.now())) {

            // 오늘이면 현재 시간 보다 이후의 회차 시간
            roundTimes = roundTimeRepository.findAllByRoundDateAndTimeAfter(
                    RoundDate.builder().idx(roundDateIdx).build(), LocalTime.now()
            );

        } else {

            // 그게 아니면 그냥 회차 시간
            roundTimes = roundTimeRepository.findAllByRoundDate(RoundDate.builder().idx(roundDateIdx).build());
        }


        return RoundTimesResult.from(roundTimes);
    }
}
