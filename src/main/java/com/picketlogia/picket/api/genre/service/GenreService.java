package com.picketlogia.picket.api.genre.service;

import com.picketlogia.picket.api.genre.dto.response.GenreListResponse;
import com.picketlogia.picket.api.genre.dto.result.GenreResult;
import com.picketlogia.picket.api.genre.model.Genre;
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

    public GenreListResponse findAll() {
        List<Genre> result = genreRepository.findAll();

        return GenreListResponse.from(result);
    }

    public GenreResult findByCode(String code) {
        Optional<Genre> result = genreRepository.findByCode(code);

        Genre findGenre = result.orElseThrow(NoSuchElementException::new);
        return GenreResult.from(findGenre);
    }
}
