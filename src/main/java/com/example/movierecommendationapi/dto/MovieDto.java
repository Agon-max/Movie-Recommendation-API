package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MovieDto {
    private Long id;
    private Long tmdbId;
    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String overview;

    @Size(max = 20)
    private String language;

    private List<Long> directorIds  = new ArrayList<Long>();

    private List<Long> genreIds = new ArrayList<Long>();

    private List<Long> actorIds = new ArrayList<Long>();

    private String releaseDate;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double averageRating;
}
