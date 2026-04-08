package com.picketlogia.picket.api.reservation.service;

import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.model.RoundDate;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.api.reservation.model.ReservationCheck;
import com.picketlogia.picket.api.reservation.model.ReservationRegister;
import com.picketlogia.picket.api.reservation.model.ReserveDetailRegister;
import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.reservation.repository.ReservationRepository;
import com.picketlogia.picket.api.reservation.repository.ReserveDetailRepository;
import com.picketlogia.picket.api.seat.model.Seat;
import com.picketlogia.picket.api.seat.model.SeatGrade;
import com.picketlogia.picket.api.seat.model.SeatGradeStatus;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.common.exception.BaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReserveDetailRepository reserveDetailRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void register() {
        // given
        LocalDateTime paidAt = LocalDateTime.now();
        LocalDate roundDate = LocalDate.now();
        LocalTime roundTime = LocalTime.now();

        Reservation willReserve = Reservation.builder()
                .idx(1L)
                .paidAt(paidAt)
                .price(10000L)
                .paymentIdx("TEMPID0110330")
                .product(
                        Product.builder().idx(1L).name("상품01").build()
                )
                .user(
                        User.builder().idx(1L).name("유저01").build()
                )
                .build();

        List<ReserveDetail> willReserveDetails = List.of(
                ReserveDetail.builder().idx(1L)
                        .seat(
                                Seat.builder().idx(1L).name("A-01")
                                        .seatGrade(
                                                SeatGrade
                                                        .builder()
                                                        .idx(1L)
                                                        .price(10000L)
                                                        .grade(SeatGradeStatus.S)
                                                        .build()
                                        ).build()
                        )
                        .roundTime(
                                RoundTime.builder().idx(1L).time(roundTime)
                                        .roundDate(
                                                RoundDate.builder().idx(1L).date(roundDate).build()
                                        ).build()
                        ).build()

        );

        ReservationRegister register = ReservationRegister.builder()
                .userIdx(1L)
                .productIdx(1L)
                .paymentIdx("TEMPID0110330")
                .price(10000L)
                .paidAt(paidAt)
                .build();

        ReserveDetailRegister detailRegister = ReserveDetailRegister.builder()
                .roundTimeIdx(1L)
                .reservationIdx(1L)
                .seatIdes(List.of(1L))
                .build();

        given(reservationRepository.save(any(Reservation.class))).willReturn(willReserve);
        given(reserveDetailRepository.saveAll(anyList())).willReturn(willReserveDetails);

//        // when
//        ReservationReadForDetail response = reservationService.register(register, detailRegister);
//
//        // then
//        assertThat(response.getIdx()).isEqualTo(1L);
//        assertThat(response.getPaymentIdx()).isEqualTo("TEMPID0110330");
//        assertThat(response.getProductName()).isEqualTo("상품01");
//        assertThat(response.getTotalPrice().getPrice()).isEqualTo(10000L);
//        assertThat(response.getTotalPrice().getPriceFormat()).isEqualTo("10,000원");
//        assertThat(response.getDetails().size()).isEqualTo(1);
//
//        assertThat(response.getDetails().get(0).getIdx()).isEqualTo(1L);
//        assertThat(response.getDetails().get(0).getRoundDate()).isEqualTo(roundDate);
//        assertThat(response.getDetails().get(0).getRoundTime()).isEqualTo(roundTime);
//        assertThat(response.getDetails().get(0).getSeatName()).isEqualTo("A-01");
//        assertThat(response.getDetails().get(0).getGrade()).isEqualTo(SeatGradeStatus.S);
//        assertThat(response.getDetails().get(0).getPrice().getPrice()).isEqualTo(10000L);
//        assertThat(response.getDetails().get(0).getPrice().getPriceFormat()).isEqualTo("10,000원");

    }

    @Test
    @DisplayName("유저가 구매한 내역이 있다.")
    void hasPurchasedProduct() {
        // given
        Long userIdx = 1L;
        Long productIdx = 1L;

        given(reservationRepository.countByUserAndProduct(any(User.class), any(Product.class))).willReturn(1L);

        // when
        Boolean hasPurchasedProduct = reservationService.hasPurchasedProduct(userIdx, productIdx);

        // then
        assertTrue(hasPurchasedProduct);
    }

    @Test
    @DisplayName("유저가 구매한 내역이 없다.")
    void hasNotPurchasedProduct() {
        // given
        Long userIdx = 1L;
        Long productIdx = 1L;

        given(reservationRepository.countByUserAndProduct(any(User.class), any(Product.class))).willReturn(0L);

        // when
        Boolean hasPurchasedProduct = reservationService.hasPurchasedProduct(userIdx, productIdx);

        // then
        assertFalse(hasPurchasedProduct);
    }

    @Nested
    class checkReservedAlready {

        private static Long roundTimeIdx;
        private static List<ReserveDetail> alreadyReservedSeats;

        @BeforeAll
        static void setUp() {
            roundTimeIdx = 1L;
            alreadyReservedSeats = List.of(
                    ReserveDetail.builder()
                            .roundTime(RoundTime.builder().idx(roundTimeIdx).build())
                            .seat(Seat.builder().idx(1L).build())
                            .build(),
                    ReserveDetail.builder()
                            .roundTime(RoundTime.builder().idx(roundTimeIdx).build())
                            .seat(Seat.builder().idx(2L).build())
                            .build(),
                    ReserveDetail.builder()
                            .roundTime(RoundTime.builder().idx(roundTimeIdx).build())
                            .seat(Seat.builder().idx(3L).build())
                            .build(),
                    ReserveDetail.builder()
                            .roundTime(RoundTime.builder().idx(roundTimeIdx).build())
                            .seat(Seat.builder().idx(4L).build())
                            .build()
            );
        }

        @Test
        @DisplayName("이미 결제 완료된 좌석을 구매하려 한다.")
        void hasAlreadySeat() {
            // given


            ReservationCheck request = ReservationCheck.builder()
                    .roundTimeIdx(roundTimeIdx)
                    .seatIdxes(List.of(1L, 10L, 20L, 12L))
                    .build();

            given(reserveDetailRepository.findAllByRoundTime(any(RoundTime.class))).willReturn(alreadyReservedSeats);

            // when
            // then
            assertThrows(BaseException.class, () -> reservationService.checkReservedSeat(request));
        }

        @Test
        @DisplayName("모든 좌석 결제 가능")
        void hasNotReservedAlreadySeat() {
            // given
            ReservationCheck request = ReservationCheck.builder()
                    .roundTimeIdx(roundTimeIdx)
                    .seatIdxes(List.of(10L, 20L, 12L))
                    .build();

            given(reserveDetailRepository.findAllByRoundTime(any(RoundTime.class))).willReturn(alreadyReservedSeats);

            // when
            // then
            assertDoesNotThrow(() -> reservationService.checkReservedSeat(request));
        }
    }

}