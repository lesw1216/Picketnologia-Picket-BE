package com.picketlogia.picket.api.seat.dto.request;

import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatRequest {

    private String name;
    private SeatGradeStatus grade;
}
