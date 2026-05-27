package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.seat.dto.command.SeatSaveCommand;
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

    /**
     * 좌석 명령 목록을 등급 매핑에 맞춰 Seat 엔티티로 변환해 일괄 저장한다.
     *
     * @param productIdx    좌석이 속할 공연 상품 ID
     * @param seatCommands  행 단위로 묶인 좌석 저장 명령 목록
     * @param seatGradeMap  등급별 ID 매핑 (저장된 SeatGrade 결과)
     */
    public void saveAll(Long productIdx,
                        List<List<SeatSaveCommand>> seatCommands,
                        Map<SeatGradeStatus, Long> seatGradeMap) {

        List<Seat> seats = seatCommands.stream()
                .flatMap(Collection::stream)
                .map(command -> {
                    Long gradeIdx = seatGradeMap.get(command.getGrade());
                    return command.toEntity(gradeIdx, productIdx);
                }).toList();

        seatRepository.saveAll(seats);
    }
}
