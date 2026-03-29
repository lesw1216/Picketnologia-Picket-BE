package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.reservation.service.ReserveDetailService;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.dto.read.SeatRead;
import com.picketlogia.picket.api.seat.dto.register.SeatRegister;
import com.picketlogia.picket.api.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ReserveDetailService reserveDetailService;

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

    public List<List<SeatRead>> findAllByProductId(Long productIdx, Long roundTimeIdx) {

        List<Seat> findSeats = seatRepository.findAllByProductWithSeatGrade(
                Product.builder()
                        .idx(productIdx)
                        .build()
        );

        List<Long> reservedSeats = reserveDetailService.findReservedSeats(roundTimeIdx);


        LinkedHashMap<Character, List<SeatRead>> grouped =
                findSeats.stream().map(seat ->
                                SeatRead.from(seat, reservedSeats.contains(seat.getIdx())))
                        .collect(Collectors.groupingBy(
                                seatRead -> seatRead.getName().charAt(0), LinkedHashMap::new, Collectors.toList()
        ));

        return new ArrayList<>(grouped.values());

    }
}
