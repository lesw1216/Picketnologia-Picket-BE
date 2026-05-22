package com.picketlogia.picket.api.genre.dto.response;

import com.picketlogia.picket.api.genre.dto.result.GenreResult;
import com.picketlogia.picket.api.genre.model.Genre;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GenreListResponse {
    List<GenreResult> genres;

    public static GenreListResponse from(List<Genre> genres) {
        return GenreListResponse.builder()
                .genres(genres.stream().map(GenreResult::from).toList())
                .build();
    }
}
