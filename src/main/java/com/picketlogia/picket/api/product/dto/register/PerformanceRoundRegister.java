package com.picketlogia.picket.api.product.dto.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceRoundRegister {

    private LocalDate startDate;
    private LocalDate endDate;
    private List<SelectedDay> selectedDays;
    private List<LocalTime> sameTimes;
    private List<ManualRound> manualRounds;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedDay {
        private String code;
        private List<LocalTime> times;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManualRound {
        private LocalDate date;
        private LocalTime time;
    }
}
