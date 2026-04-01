package com.picketlogia.picket.api.reservation.service;

import com.picketlogia.picket.api.payments.model.PaymentStatusResponse;
import com.picketlogia.picket.api.product.model.entity.Product;
import com.picketlogia.picket.api.product.model.entity.RoundTime;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.model.ReservationCheck;
import com.picketlogia.picket.api.reservation.model.ReservationRegister;
import com.picketlogia.picket.api.reservation.model.UpdateReservationReq;
import com.picketlogia.picket.api.reservation.model.dto.ReservationListDto;
import com.picketlogia.picket.api.reservation.model.entity.Reservation;
import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.reservation.repository.ReservationRepository;
import com.picketlogia.picket.api.reservation.repository.ReserveDetailRepository;
import com.picketlogia.picket.api.seat.service.SeatHoldService;
import com.picketlogia.picket.api.user.model.entity.User;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReserveDetailRepository reserveDetailRepository;
    private final SeatHoldService seatHoldService;

    /**
     * 예매 정보를 저장한다.
     * @param reservationRegister 예매 정보
     */
    public Long register(ReservationRegister reservationRegister) {

        // 예매 저장
        Reservation savedReservation = reservationRepository.save(reservationRegister.toEntity());

        return savedReservation.getIdx();
    }

    @Transactional
    public Long updateReservation(UpdateReservationReq update, String paymentIdx) {
        Optional<Reservation> result = reservationRepository.findByPaymentIdx(paymentIdx);


        Reservation findReserve = result.orElseThrow(() -> BaseException.from(BaseResponseStatus.NOT_FOUND_DATA));
        findReserve.completeReservation(update);
        findReserve.getProduct().incrementSalesCount();

        return findReserve.getIdx();
    }

    /**
     * 해당 상품을 구매한 적이 있는지 검증한다.
     * @param userIdx 검증할 유저의 IDX
     * @param productIdx 검증할 상품의 IDX
     * @return 구매한 내역이 있다면 <code>true</code>, 없다면 <code>false</code>
     */
    public Boolean hasPurchasedProduct(Long userIdx, Long productIdx) {
        Long countByUser = reservationRepository.countByUserAndProduct(
                User.builder().idx(userIdx).build(),
                Product.builder().idx(productIdx).build()
        );

        return countByUser > 0;
    }

    /**
     * 구매하려는 좌석이 이미 예약되어 있는 좌석인지 검증한다.
     * @param reservationCheck 예약 요청 정보
     * @throws BaseException 이미 구매된 좌석이 포함되었다면 예외 발생
     */
    public void checkReservedSeat(ReservationCheck reservationCheck) {
        /*
        * 이전에 구매한 예약 좌석 검증은 다음과 같은 idx가 필요하다.
        * 1. roundTimeIdx - 회차
        * 2. seatIdx
        * */

        // 회차 IDX를 사용해 해당 회차의 예약 상세 목록을 조회한다.
        List<ReserveDetail> allByRoundTime = reserveDetailRepository.findAllByRoundTime(
                RoundTime.builder().idx(reservationCheck.getRoundTimeIdx()).build()
        );

        /*
         * 1. 조회한 상세 목록에서 좌석 IDX만 추출한다.
         * 2. 예약 요청의 좌석 IDX가 포함되는지 검증한다.
         */
        List<Long> reservedSeats = allByRoundTime.stream().map(
                reserveDetail -> reserveDetail.getSeat().getIdx()
        ).toList();

        boolean hasAlreadySeats = reservationCheck.getSeatIdxes().stream().anyMatch(reservedSeats::contains);

        if (hasAlreadySeats) {
            throw BaseException.from(BaseResponseStatus.SEAT_ALREADY_BOOKED);
        }
    }

    public List<ReservationListDto> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Reservation> result = reservationRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return result.stream().map(ReservationListDto::from).toList();
    }

    public void checkRockSeats(ReservationCheck reservationCheck) {
        seatHoldService.validateRockSeats(
                reservationCheck.getRoundTimeIdx(),
                reservationCheck.getSeatIdxes().stream().map(String::valueOf).toList()
        );
    }

    public PaymentStatusResponse findPaymentStatusOfReservation(String paymentId, Long userIdx) {

        PaymentStatus findPaymentStatus = reservationRepository.findStatusByPaymentIdxAndUserId(paymentId, userIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.NOT_FOUND_DATA));

        return PaymentStatusResponse.from(findPaymentStatus);
    }

}
