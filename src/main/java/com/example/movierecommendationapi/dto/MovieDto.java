package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MovieDto {

    private Long id;
    private String title;
    private String description;
    private String director;
    private LocalDateTime releaseDate;
    private List<String> actors;
    private Double averageRating;

}
