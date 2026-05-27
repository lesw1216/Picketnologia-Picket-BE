package com.picketlogia.picket.api.seat.repository;

import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class SeatStatusRepository {

    private final StringRedisTemplate redisTemplate;

    /**
     * 좌석 상태 hash의 단일 필드값을 갱신한다 (legacy 경로).
     *
     * @param key    hash key
     * @param seatId 좌석 ID
     * @param status 새 상태값
     */
    public void updateSeatStatus(String key, String seatId, String status) {

        redisTemplate.opsForHash().put(key, seatId, status);
    }

    /**
     * 회차 시간 기준 좌석 상태 hash의 필드값을 갱신한다.
     *
     * @param key     hash key
     * @param seatIdx 좌석 ID
     * @param status  새 상태값
     */
    public void updateSeatStatusV2(String key, String seatIdx, String status) {

        redisTemplate.opsForHash().put(key, seatIdx, status);
    }

    /**
     * 주어진 key에 만료 시간을 설정한다.
     *
     * @param key     대상 Redis key
     * @param timeout 만료 시간
     * @param unit    시간 단위
     */
    public void expire(String key, long timeout, TimeUnit unit) {

        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 특정 회차의 임시 선점 좌석 hash 전체를 조회한다.
     *
     * @param key 회차로 구성된 hash key
     * @return 좌석 ID-상태 맵
     */
    public Map<Object, Object> findHeldSeatsByRoundTime(String key) {

        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 좌석 상태 hash에서 특정 좌석 필드를 제거한다.
     *
     * @param key    hash key
     * @param seatId 제거 대상 좌석 ID
     */
    public void deleteSeatStatus(String key, String seatId) {

        redisTemplate.opsForHash().delete(key, seatId);
    }

    /**
     * 좌석 ID 목록이 모두 hash에 존재하는지 검증한다.
     *
     * @param key       hash key
     * @param seatIdxes 검증할 좌석 ID 목록
     * @throws BaseException 좌석 잠금이 누락되었을 때
     */
    public void allFieldsExist(String key, List<String> seatIdxes) {

        boolean result = seatIdxes.stream().allMatch((seatIdx) -> redisTemplate.opsForHash().hasKey(key, seatIdx));

        if (!result) {
            throw BaseException.from(BaseResponseStatus.NOT_ROCKED_SEAT);
        }
    }

    /**
     * 좌석 단위 분리 잠금 키를 TTL과 함께 저장한다.
     *
     * @param key     좌석 잠금 키
     * @param timeout TTL (밀리초)
     */
    public void saveSeparateSeat(String key, Long timeout) {

        redisTemplate.opsForValue().set(key, "rocked", timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 좌석 단위 분리 잠금 키를 즉시 제거한다.
     *
     * @param key 좌석 잠금 키
     */
    public void deleteSeparateSeat(String key) {

        redisTemplate.delete(key);
    }

    /**
     * Redis hash에서 회차에 속한 특정 좌석 항목들을 일괄 삭제한다.
     *
     * @param key     hash key
     * @param seatIds 삭제할 좌석 ID 목록
     */
    public void deleteHeldSeatsByRoundIdAndSeatIds(String key, List<String> seatIds) {

        redisTemplate.opsForHash().delete(key, seatIds.toArray());
    }
}
