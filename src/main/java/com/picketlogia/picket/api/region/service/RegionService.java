package com.picketlogia.picket.api.region.service;

import com.picketlogia.picket.api.region.model.Region;
import com.picketlogia.picket.api.region.dto.result.RegionResults;
import com.picketlogia.picket.api.region.repository.RegionRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    /**
     * 전체 지역 목록을 조회한다.
     * @return <code>RegionList</code> 전체 지역 목록 읽기 전용 DTO
     */
    public RegionResults findAll() {

        List<Region> regions = regionRepository.findAll();
        return RegionResults.of(regions);
    }

    /**
     * 요청을 받은 지역 코드로 지역 IDX를 찾는다.
     * @param code IDX를 찾고 싶은 지역 코드
     * @return 지역 코드의 <code>IDX</code>
     * @throws BaseException 지역 코드가 존재하지 않는 경우 예외 발생
     */
    public Integer findIdxByCode(String code) {

        Optional<Region> result = regionRepository.findByCode(code);

        Region region = result.orElseThrow(() -> BaseException.from(BaseResponseStatus.INVALID_REGION_CODE));
        return region.getIdx();
    }
}
