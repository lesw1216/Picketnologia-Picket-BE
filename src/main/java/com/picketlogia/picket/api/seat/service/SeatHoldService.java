package com.picketlogia.picket.api.seat.service;

import com.picketlogia.picket.api.seat.dto.command.ReleaseHeldSeatsCommand;
import com.picketlogia.picket.api.seat.repository.SeatStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatHoldService {

    private final SeatStatusRepository seatStatusRepository;
    private static final long SEAT_STATUS_TTL_MILLISECONDS = 1000 * 60 * 10;

    /**
     * 회차 + 날짜 조합 키 기준으로 좌석 상태를 갱신한다 (legacy 경로).
     *
     * @param roundId  회차 ID
     * @param datetime 회차 날짜 식별자
     * @param seatId   대상 좌석 ID
     * @param status   업데이트할 상태값
     */
    public void updateSeatStatus(Long roundId, String datetime, String seatId, String status) {

        String key = "seat-status : " + roundId + "-" + datetime;
        seatStatusRepository.updateSeatStatus(key, seatId, status);
    }

    /**
     * 회차 시간 기준 hash에 좌석 상태를 갱신한다.
     *
     * @param roundTimeIdx 회차 시간 ID
     * @param seatId       대상 좌석 ID
     * @param status       업데이트할 상태값
     */
    public void updateSeatStatusV2(Long roundTimeIdx, String seatId, String status) {

        String key = createKey(roundTimeIdx);
        seatStatusRepository.updateSeatStatusV2(key, seatId, status);
    }

    /**
     * 사용자가 임시 선점한 좌석들을 해제한다.
     *
     * @param command 해제 대상 회차와 좌석 ID 목록
     */
    public void releaseHeldSeats(ReleaseHeldSeatsCommand command) {

        String key = createKey(command.getRoundTimeId());
        seatStatusRepository.deleteHeldSeatsByRoundIdAndSeatIds(
                key,
                command.getSeatIds().stream().map(String::valueOf).toList()
        );
    }

    /**
     * 결제 전 임시 선점된 좌석 정보를 Redis에서 조회한다.
     *
     * @param roundId 조회할 회차 ID
     * @return 선점된 좌석 ID-상태 맵
     */
    public Map<Object, Object> getHeldSeats(Long roundId) {

        String key = createKey(roundId);
        return seatStatusRepository.findHeldSeatsByRoundTime(key);
    }

    /**
     * 회차 hash에서 특정 좌석 항목을 제거한다.
     *
     * @param roundTimeIdx 회차 시간 ID
     * @param seatIdx      제거 대상 좌석 ID
     */
    public void deleteSeatStatus(Long roundTimeIdx, String seatIdx) {

        String key = createKey(roundTimeIdx);
        seatStatusRepository.deleteSeatStatus(key, seatIdx);
    }

    /**
     * 결제 시점에 좌석 잠금이 유효한지 검증한다.
     *
     * @param roundTimeIdx 회차 시간 ID
     * @param seatIdxes    검증할 좌석 ID 목록
     * @throws com.picketlogia.picket.common.exception.BaseException 좌석 잠금이 만료되었을 때
     */
    public void validateRockSeats(Long roundTimeIdx, List<String> seatIdxes) {

        String key = createKey(roundTimeIdx);
        seatStatusRepository.allFieldsExist(key, seatIdxes);
    }

    /**
     * 좌석 단위로 분리된 잠금 키를 TTL과 함께 저장한다.
     *
     * @param roundTimeIdx 회차 시간 ID
     * @param seatIdx      잠금을 거는 좌석 ID
     */
    public void saveSeparateSeat(Long roundTimeIdx, String seatIdx) {

        String key = "seat:" + roundTimeIdx + "-" + seatIdx;
        seatStatusRepository.saveSeparateSeat(key, SEAT_STATUS_TTL_MILLISECONDS);
    }

    /**
     * 좌석 단위 분리 잠금 키를 즉시 제거한다.
     *
     * @param roundTimeIdx 회차 시간 ID
     * @param seatIdx      잠금을 해제할 좌석 ID
     */
    public void deleteSeparateSeat(Long roundTimeIdx, String seatIdx) {

        String key = "seat:" + roundTimeIdx + "-" + seatIdx;
        seatStatusRepository.deleteSeparateSeat(key);
    }

    private String createKey(Long roundTimeIdx) {

        return "seat-status : " + roundTimeIdx;
    }
}
