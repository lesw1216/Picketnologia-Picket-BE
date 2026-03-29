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

    public void updateSeatStatus(String key, String seatId, String status) {
        redisTemplate.opsForHash().put(key, seatId, status);
    }

    public void updateSeatStatusV2(String key, String seatIdx, String status) {
        redisTemplate.opsForHash().put(key, seatIdx, status);
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * Redis에 저장된 임시 좌석 정보를 조회합니다.
     *
     * @param key 회차 ID로 구성된 Hash의 key
     * @return 조회된 좌석 정보
     */
    public Map<Object, Object> findRockedSeatsByRoundTime(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void deleteSeatStatus(String key, String seatId) {
        redisTemplate.opsForHash().delete(key, seatId);
    }

    public void allFieldsExist(String key, List<String> seatIdxes) {
        boolean result = seatIdxes.stream().allMatch((seatIdx) -> redisTemplate.opsForHash().hasKey(key, seatIdx));

        if (!result) {
            throw BaseException.from(BaseResponseStatus.NOT_ROCKED_SEAT);
        }
    }

    public void saveSeparateSeat(String key, Long timeout) {
        redisTemplate.opsForValue().set(key, "rocked", timeout, TimeUnit.MILLISECONDS);
    }

    public void deleteSeparateSeat(String key) {
        redisTemplate.delete(key);

    }

    /**
     * 특정 회차의 특정 유저가 웹소켓이 연결한 상태로 잠근 좌석을 삭제
     *
     * @param key       해시맵으로 구성된 value을 가지는 키
     * @param seatIdxes 삭제할 좌석 idx
     */
    public void deleteAllRockedSeatsByRoundTime(String key, List<String> seatIdxes) {
        redisTemplate.opsForHash().delete(key, seatIdxes.toArray());
    }
}