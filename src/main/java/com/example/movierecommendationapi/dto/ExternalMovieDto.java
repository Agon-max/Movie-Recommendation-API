package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalMovieDto {

    private Long id;
    private Long tmbdId;
    private String title;
    private String posterUrl;
    private String language;
    private Integer runtime;
    private String overview;
    private Long movieId;
}
