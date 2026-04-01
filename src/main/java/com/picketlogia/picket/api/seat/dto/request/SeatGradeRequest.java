package com.picketlogia.picket.api.seat.dto.request;

import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatGradeRequest {

    private SeatGradeStatus code;
    private Long price;
}
