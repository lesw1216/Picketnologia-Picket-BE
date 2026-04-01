package com.picketlogia.picket.api.reservation.model;

import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.seat.dto.response.PriceResponse;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReserveDetailRead {
    private Long idx;
    private SeatGradeStatus grade;
    private String seatName;
    private PriceResponse price;
    private LocalTime roundTime;
    private LocalDate roundDate;

    public static ReserveDetailRead from(ReserveDetail entity) {
        return ReserveDetailRead.builder()
                .idx(entity.getIdx())
                .grade(entity.getSeat().getSeatGrade().getGrade())
                .price(
                        PriceResponse.from(entity.getSeat().getSeatGrade().getPrice())
                )
                .seatName(entity.getSeat().getName())
                .roundDate(entity.getRoundTime().getRoundDate().getDate())
                .roundTime(entity.getRoundTime().getTime())
                .build();
    }
}
