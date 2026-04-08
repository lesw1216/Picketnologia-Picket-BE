package com.picketlogia.picket.api.genre.dto.result;

import com.picketlogia.picket.api.genre.model.Genre;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenreReadResponse {
    private int idx;
    private String code;
    private String name;

    public static GenreReadResponse from(Genre entity) {
        return GenreReadResponse.builder()
                .idx(entity.getIdx())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }
}
