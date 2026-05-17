package com.example.movierecommendationapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbMovieDto {

    private Long id;

    private String title;

    private String overview;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("genre_ids")
    private List<Long> genreIds;


}
