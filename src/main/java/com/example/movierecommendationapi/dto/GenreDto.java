package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreDto {
    private Long id;
    private Long tmdbId;
    @NotBlank
    private String name;
}
