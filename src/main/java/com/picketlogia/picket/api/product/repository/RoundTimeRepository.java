package com.picketlogia.picket.api.product.repository;

import com.picketlogia.picket.api.product.model.RoundDate;
import com.picketlogia.picket.api.product.model.RoundTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface RoundTimeRepository extends JpaRepository<RoundTime, Long> {

    List<RoundTime> findAllByRoundDateAndTimeAfter(RoundDate roundDate, LocalTime time);
    List<RoundTime> findAllByRoundDate(RoundDate roundDate);
}
