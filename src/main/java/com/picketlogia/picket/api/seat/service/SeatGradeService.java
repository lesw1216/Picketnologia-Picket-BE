package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.dto.read.SeatGradeRead;
import com.picketlogia.picket.api.seat.dto.register.SeatGradeRegister;
import com.picketlogia.picket.api.seat.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatGradeService {

    private final SeatGradeRepository seatGradeRepository;

    // 전체 한번에 저장 후 Map 반환
    public Map<SeatGradeStatus, Long> saveAll(Long productIdx, List<SeatGradeRegister> registers) {

        List<SeatGrade> savedGrades = seatGradeRepository.saveAll(
                registers.stream().map(seatGrade -> seatGrade.toEntity(productIdx)).toList()
        );

        return savedGrades.stream().collect(
                Collectors.toMap(SeatGrade::getGrade, SeatGrade::getIdx)
        );
    }

    // 전체 조회
    public List<SeatGradeRead> findAllByProduct(Long productIdx) {

        List<SeatGrade> findSeatGrades = seatGradeRepository.findAllByProduct(
                Product.builder()
                        .idx(productIdx)
                        .build()
        );

        return findSeatGrades.stream().map(SeatGradeRead::from).toList();
    }
}
