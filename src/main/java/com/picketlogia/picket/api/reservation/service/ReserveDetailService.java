package com.picketlogia.picket.api.reservation.service;

import com.picketlogia.picket.api.reservation.model.ReserveDetailRegister;
import com.picketlogia.picket.api.reservation.model.entity.ReserveDetail;
import com.picketlogia.picket.api.reservation.repository.ReserveDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveDetailService {

    private final ReserveDetailRepository reserveDetailRepository;

    /**
     * 예매 상세 목록을 저장한다.
     * @param reserveDetailRegister 예매 상세 목록
     * @return <code>List<<code>Long</code>></code> 저장된 예매 상세 목록들의 <code>IDX</code> 반환
     */
    public List<Long> register(ReserveDetailRegister reserveDetailRegister) {
        List<ReserveDetail> reserveDetails = reserveDetailRepository.saveAll(reserveDetailRegister.toEntities());
        return reserveDetails.stream().map(ReserveDetail::getIdx).toList();
    }
}
