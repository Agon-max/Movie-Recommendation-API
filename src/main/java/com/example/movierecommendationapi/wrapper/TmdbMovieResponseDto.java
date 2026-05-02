package com.example.movierecommendationapi.wrapper;

import com.example.movierecommendationapi.dto.TmdbMovieDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbMovieResponseDto {
    private Integer page;
    private List<TmdbMovieDto> results;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_results")
    private Integer totalResults;
}
