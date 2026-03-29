package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.dto.read.SeatGradeRead;
import com.picketlogia.picket.api.seat.dto.read.SeatInfo;
import com.picketlogia.picket.api.seat.dto.read.SeatRead;
import com.picketlogia.picket.api.seat.dto.register.SeatGradeRegister;
import com.picketlogia.picket.api.seat.dto.register.SeatRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatInfoService {

    private final SeatService seatService;
    private final SeatGradeService seatGradeService;

    public void save(Long productIdx, List<SeatGradeRegister> seatGradeRegisters, List<List<SeatRegister>> seatRegisters) {
        Map<SeatGradeStatus, Long> seatGradeMap = seatGradeService.saveAll(productIdx, seatGradeRegisters);
        seatService.saveAll(productIdx, seatRegisters, seatGradeMap);
    }

    public SeatInfo findSeatInfo(Long productId, Long reservedIdx) {
        List<SeatGradeRead> seatGrades = seatGradeService.findAllByProduct(productId);
        List<List<SeatRead>> seats = seatService.findAllByProductId(productId, reservedIdx);

        return SeatInfo.from(seatGrades, seats);
    }
}
