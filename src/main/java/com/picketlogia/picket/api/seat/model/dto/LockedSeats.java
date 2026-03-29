package com.picketlogia.picket.api.seat.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LockedSeats {
    private List<Long> seatIds;
}
