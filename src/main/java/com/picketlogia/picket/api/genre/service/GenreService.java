package com.picketlogia.picket.api.genre.service;

import com.picketlogia.picket.api.genre.dto.response.GenreListResponse;
import com.picketlogia.picket.api.genre.dto.result.GenreResult;
import com.picketlogia.picket.api.genre.model.Genre;
import com.picketlogia.picket.api.genre.repository.GenreRepository;
import com.picketlogia.picket.common.exception.BaseException;
import com.picketlogia.picket.common.model.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    /**
     * 등록된 장르 전체를 조회한다.
     *
     * @return 장르 목록 응답
     */
    public GenreListResponse findAll() {

        List<Genre> result = genreRepository.findAll();

        return GenreListResponse.from(result);
    }

    /**
     * 장르 코드로 단일 장르를 조회한다.
     *
     * @param code 장르 코드
     * @return 장르 결과
     * @throws BaseException 코드에 해당하는 장르가 없을 때
     */
    public GenreResult findByCode(String code) {

        Genre findGenre = genreRepository.findByCode(code)
                .orElseThrow(() -> BaseException.from(BaseResponseStatus.GENRE_NOT_FOUND));

        return GenreResult.from(findGenre);
    }
}
