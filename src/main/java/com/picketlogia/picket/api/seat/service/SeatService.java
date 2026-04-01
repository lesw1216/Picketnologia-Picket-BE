package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.seat.dto.register.SeatRegister;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    // 저장
    public void saveAll(Long productIdx, List<List<SeatRegister>> seatRegisters, Map<SeatGradeStatus, Long> seatGradeMap) {
        List<Seat> seats = seatRegisters.stream()
                .flatMap(Collection::stream)
                .map(seat -> {
                    Long gradeIdx = seatGradeMap.get(seat.getGrade());
                    return seat.toEntity(gradeIdx, productIdx);
                }).toList();

        seatRepository.saveAll(seats);
    }
}
