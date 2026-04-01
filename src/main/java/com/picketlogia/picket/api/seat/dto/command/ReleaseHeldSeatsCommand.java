package com.picketlogia.picket.api.seat.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReleaseHeldSeatsCommand {

    private Long roundTimeId;
    private List<Long> seatIds;
}
