package com.picketlogia.picket.api.reservation.service;

import com.picketlogia.picket.api.reservation.dto.command.ReserveDetailRegisterCommand;
import com.picketlogia.picket.api.reservation.model.ReserveDetail;
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
     * @param reserveDetailRegisterCommand 예매 상세 목록
     * @return <code>List<<code>Long</code>></code> 저장된 예매 상세 목록들의 <code>IDX</code> 반환
     */
    public List<Long> register(ReserveDetailRegisterCommand reserveDetailRegisterCommand) {

        List<ReserveDetail> reserveDetails = reserveDetailRepository.saveAll(reserveDetailRegisterCommand.toEntities());
        return reserveDetails.stream().map(ReserveDetail::getIdx).toList();
    }
}
