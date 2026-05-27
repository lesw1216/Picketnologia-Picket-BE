package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.seat.dto.command.SeatGradeSaveCommand;
import com.picketlogia.picket.api.seat.repository.SeatGradeRepository;
import com.picketlogia.picket.api.seat.dto.result.SeatGradeResult;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatGradeService {

    private final SeatGradeRepository seatGradeRepository;

    /**
     * 좌석 등급 명령 목록을 일괄 저장하고 등급별 ID 매핑을 반환한다.
     *
     * @param productIdx 좌석 등급이 속할 공연 상품 ID
     * @param commands   저장할 좌석 등급 명령 목록
     * @return 좌석 등급 상태(Enum) → 저장된 SeatGrade ID 매핑
     */
    public Map<SeatGradeStatus, Long> saveAll(Long productIdx, List<SeatGradeSaveCommand> commands) {

        List<SeatGrade> savedGrades = seatGradeRepository.saveAll(
                commands.stream().map(command -> command.toEntity(productIdx)).toList()
        );

        return savedGrades.stream().collect(
                Collectors.toMap(SeatGrade::getGrade, SeatGrade::getIdx)
        );
    }

    /**
     * 특정 공연에 등록된 좌석 등급 목록을 조회한다.
     *
     * @param productIdx 조회 대상 공연 상품 ID
     * @return 좌석 등급 결과 목록
     */
    public List<SeatGradeResult> findAllByProduct(Long productIdx) {

        List<SeatGrade> findSeatGrades = seatGradeRepository.findAllByProduct(
                Product.builder()
                        .idx(productIdx)
                        .build()
        );

        return findSeatGrades.stream().map(SeatGradeResult::from).toList();
    }
}
