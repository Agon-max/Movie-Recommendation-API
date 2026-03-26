package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TMDBGenreResponseDto {
    private List<TMDBGenreDto> genres;
}
