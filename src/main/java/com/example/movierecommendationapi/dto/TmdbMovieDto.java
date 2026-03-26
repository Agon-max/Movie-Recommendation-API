package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbMovieDto {

    private Long id;
    private String title;
    private String original_title;
    private String overview;
    private String original_language;
    private String release_date;

    private Double popularity;
    private Double vote_average;
    private Integer vote_count;

    private String poster_path;
    private String backdrop_path;

    private List<Integer> genre_ids;
}
