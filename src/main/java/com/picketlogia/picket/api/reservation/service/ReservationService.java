package com.picketlogia.picket.api.reservation.service;

import com.picketlogia.picket.api.payments.dto.result.PaymentStatusResult;
import com.picketlogia.picket.api.product.model.Product;
import com.picketlogia.picket.api.product.model.RoundTime;
import com.picketlogia.picket.api.reservation.model.PaymentStatus;
import com.picketlogia.picket.api.reservation.dto.request.ReservationCheckRequest;
import com.picketlogia.picket.api.reservation.dto.command.ReservationRegisterCommand;
import com.picketlogia.picket.api.reservation.dto.request.UpdateReservationRequest;
import com.picketlogia.picket.api.reservation.dto.result.ReservationResult;
import com.picketlogia.picket.api.reservation.model.Reservation;
import com.picketlogia.picket.api.reservation.model.ReserveDetail;
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

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReserveDetailRepository reserveDetailRepository;
    private final SeatHoldService seatHoldService;

    /**
     * 예매를 신규 저장한다.
     *
     * @param reservationRegister 예매 등록 명령
     * @return 저장된 예매 ID
     */
    public Long register(ReservationRegisterCommand reservationRegister) {

        Reservation savedReservation = reservationRepository.save(reservationRegister.toEntity());

        return savedReservation.getIdx();
    }

    /**
     * 결제 ID로 예매를 찾아 완료 처리하고 상품 판매 수를 증가시킨다.
     *
     * @param update     완료 처리에 필요한 추가 정보
     * @param paymentIdx 결제 ID
     * @return 완료 처리된 예매 ID
     * @throws BaseException 결제 ID에 매칭되는 예매가 없을 때
     */
    @Transactional
    public Long updateReservation(UpdateReservationRequest update, String paymentIdx) {

        Reservation findReserve = reservationRepository.findByPaymentIdx(paymentIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.NOT_FOUND_DATA));

        findReserve.completeReservation(update);
        findReserve.getProduct().incrementSalesCount();

        return findReserve.getIdx();
    }

    /**
     * 사용자가 특정 상품을 구매한 적이 있는지 확인한다.
     *
     * @param userIdx    검증할 사용자 ID
     * @param productIdx 검증할 상품 ID
     * @return 구매 내역이 있으면 {@code true}
     */
    public Boolean hasPurchasedProduct(Long userIdx, Long productIdx) {

        Long countByUser = reservationRepository.countByUserAndProduct(
                User.builder().idx(userIdx).build(),
                Product.builder().idx(productIdx).build()
        );

        return countByUser > 0;
    }

    /**
     * 회차에 이미 예약된 좌석과 요청 좌석의 중복 여부를 검증한다.
     *
     * @param reservationCheck 좌석·회차 정보가 담긴 검증 요청
     * @throws BaseException 이미 예약된 좌석이 포함되었을 때
     */
    public void checkReservedSeat(ReservationCheckRequest reservationCheck) {

        List<ReserveDetail> allByRoundTime = reserveDetailRepository.findAllByRoundTime(
                RoundTime.builder().idx(reservationCheck.getRoundTimeIdx()).build()
        );

        List<Long> reservedSeats = allByRoundTime.stream()
                .map(reserveDetail -> reserveDetail.getSeat().getIdx())
                .toList();

        boolean hasAlreadySeats = reservationCheck.getSeatIdxes().stream().anyMatch(reservedSeats::contains);

        if (hasAlreadySeats) {
            throw BaseException.from(BaseResponseStatus.SEAT_ALREADY_BOOKED);
        }
    }

    /**
     * 사용자의 지정 기간 내 예매 내역을 조회한다.
     *
     * @param userIdx      조회 대상 사용자 ID
     * @param startDateStr 시작일 (yyyy-MM-dd)
     * @param endDateStr   종료일 (yyyy-MM-dd)
     * @return 예매 결과 목록
     */
    public List<ReservationResult> listByUserAndDateRange(Long userIdx, String startDateStr, String endDateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX);

        List<Reservation> reservations = reservationRepository.findByUserIdxAndCreatedAtBetween(userIdx, startDateTime, endDateTime);

        return reservations.stream().map(ReservationResult::from).toList();
    }

    /**
     * 좌석 잠금이 결제 시점에도 유효한지 SeatHold 단을 통해 검증한다.
     *
     * @param reservationCheck 회차·좌석 정보가 담긴 요청
     * @throws BaseException 좌석 잠금이 만료되었을 때
     */
    public void checkRockSeats(ReservationCheckRequest reservationCheck) {

        seatHoldService.validateRockSeats(
                reservationCheck.getRoundTimeIdx(),
                reservationCheck.getSeatIdxes().stream().map(String::valueOf).toList()
        );
    }

    /**
     * 결제 ID와 사용자 ID로 예매의 현재 결제 상태를 조회한다.
     *
     * @param paymentId 결제 ID
     * @param userIdx   사용자 ID
     * @return 결제 상태 결과
     * @throws BaseException 매칭되는 예매가 없을 때
     */
    public PaymentStatusResult findPaymentStatusOfReservation(String paymentId, Long userIdx) {

        PaymentStatus findPaymentStatus = reservationRepository.findStatusByPaymentIdxAndUserId(paymentId, userIdx)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.NOT_FOUND_DATA));

        return PaymentStatusResult.from(findPaymentStatus);
    }

}
