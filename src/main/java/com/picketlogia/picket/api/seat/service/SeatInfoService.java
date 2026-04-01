package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.reservation.repository.ReserveDetailRepository;
import com.picketlogia.picket.api.seat.dto.read.SeatGradeRead;
import com.picketlogia.picket.api.seat.dto.read.SeatInfo;
import com.picketlogia.picket.api.seat.dto.read.SeatRead;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatInfoService {

    private final SeatRepository seatRepository;
    private final SeatGradeService seatGradeService;
    private final ReserveDetailRepository reserveDetailRepository;

    /**
     * 상품과 회차 기준으로 좌석 등급, 좌석 목록, 예약 좌석 정보를 조합해 좌석 정보를 조회합니다.
     *
     * @param productIdx 조회할 상품 IDX
     * @param roundTimeIdx 조회할 회차 IDX
     * @return 좌석 등급과 좌석 맵이 포함된 좌석 정보
     */
    public SeatInfo findSeatInfo(Long productIdx, Long roundTimeIdx) {

        List<SeatGradeRead> seatGrades = seatGradeService.findAllByProduct(productIdx);
        List<Seat> seats = seatRepository.findAllByProductWithSeatGrade(Product.builder().idx(productIdx).build());
        Set<Long> reservedSeatIds = reserveDetailRepository.findReservedSeatIdxesByRoundTimeIdx(roundTimeIdx);

        List<List<SeatRead>> seatMap = buildSeatMap(seats, reservedSeatIds);

        return SeatInfo.from(seatGrades, seatMap);
    }

    /**
     * 좌석 목록에 예약 여부를 반영하고 행 기준으로 그룹핑해 응답용 좌석 맵을 생성합니다.
     *
     * @param seats 상품에 속한 전체 좌석 목록
     * @param reservedSeatIds 예약된 좌석 IDX 집합
     * @return 행 단위로 그룹핑된 좌석 맵
     */
    private List<List<SeatRead>> buildSeatMap(List<Seat> seats, Set<Long> reservedSeatIds) {

        LinkedHashMap<Character, List<SeatRead>> seatMapByRow = seats.stream()
                .map(seat -> SeatRead.from(seat, reservedSeatIds.contains(seat.getIdx())))
                .collect(Collectors.groupingBy(
                        seatRead -> seatRead.getName().charAt(0),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return seatMapByRow.values().stream().toList();

    }
}
