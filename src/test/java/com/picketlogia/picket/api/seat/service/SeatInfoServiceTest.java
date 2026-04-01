package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.reservation.repository.ReserveDetailRepository;
import com.picketlogia.picket.api.seat.dto.read.SeatGradeRead;
import com.picketlogia.picket.api.seat.dto.read.SeatInfo;
import com.picketlogia.picket.api.seat.dto.read.SeatRead;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.seat.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SeatInfoServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatGradeService seatGradeService;

    @Mock
    private ReserveDetailRepository reserveDetailRepository;

    @InjectMocks
    private SeatInfoService seatInfoService;

    @Test
    @DisplayName("좌석 정보 조회 시 예약 상태와 행 그룹핑을 함께 반환한다.")
    void findSeatInfo() {
        Long productIdx = 1L;
        Long roundTimeIdx = 2L;
        Product product = Product.builder().idx(productIdx).build();
        SeatGrade vipGrade = SeatGrade.builder().idx(10L).grade(SeatGradeStatus.VIP).price(150000L).product(product).build();
        SeatGrade rGrade = SeatGrade.builder().idx(11L).grade(SeatGradeStatus.R).price(120000L).product(product).build();

        List<SeatGradeRead> seatGrades = List.of(
                SeatGradeRead.from(vipGrade),
                SeatGradeRead.from(rGrade)
        );

        List<Seat> seats = List.of(
                Seat.builder().idx(100L).name("A1").seatGrade(vipGrade).product(product).build(),
                Seat.builder().idx(101L).name("A2").seatGrade(vipGrade).product(product).build(),
                Seat.builder().idx(200L).name("B1").seatGrade(rGrade).product(product).build()
        );

        given(seatGradeService.findAllByProduct(productIdx)).willReturn(seatGrades);
        given(seatRepository.findAllByProductWithSeatGrade(any(Product.class))).willReturn(seats);
        given(reserveDetailRepository.findReservedSeatIdxesByRoundTimeIdx(roundTimeIdx)).willReturn(Set.of(101L));

        SeatInfo result = seatInfoService.findSeatInfo(productIdx, roundTimeIdx);

        assertThat(result.getSeatGrades()).hasSize(2);
        assertThat(result.getSeatMap()).hasSize(2);

        assertThat(result.getSeatMap().get(0))
                .extracting(SeatRead::getName)
                .containsExactly("A1", "A2");
        assertThat(result.getSeatMap().get(0))
                .extracting(SeatRead::getIsReserved)
                .containsExactly(false, true);

        assertThat(result.getSeatMap().get(1))
                .extracting(SeatRead::getName)
                .containsExactly("B1");
        assertThat(result.getSeatMap().get(1).get(0).getIsReserved()).isFalse();
        assertThat(result.getSeatMap().get(0).get(0).getPriceInfo().getPriceFormat()).isEqualTo("150,000원");
    }
}
