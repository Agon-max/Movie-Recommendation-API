package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TmdbMovieResponseDto {
    public Integer page;
    private List<TmdbMovieDto> results;
    private Integer total_pages;
    private Integer total_results;
}
