package com.picketlogia.picket.api.genre.service;

import com.picketlogia.picket.api.genre.model.Genre;
import com.picketlogia.picket.api.genre.dto.result.GenreListResponse;
import com.picketlogia.picket.api.genre.dto.result.GenreReadResponse;
import com.picketlogia.picket.api.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    // 전체 조회
    public GenreListResponse findAll() {
        List<Genre> result = genreRepository.findAll();

        return GenreListResponse.from(result);
    }

    // code로 조회
    public GenreReadResponse findByCode(String code) {
        Optional<Genre> result = genreRepository.findByCode(code);

        Genre findGenre = result.orElseThrow(NoSuchElementException::new);
        return GenreReadResponse.from(findGenre);
    }
}
