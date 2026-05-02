package com.example.movierecommendationapi.wrapper;

import com.example.movierecommendationapi.dto.TmdbGenreDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbGenreResponseDto {
    private List<TmdbGenreDto> genres;
}
