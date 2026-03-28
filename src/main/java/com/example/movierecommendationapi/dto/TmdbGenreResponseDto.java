package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbGenreResponseDto {
    private List<TmdbGenreDto> genres;
}
