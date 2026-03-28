package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.NotBlank;

public class GenreDto {
    @NotBlank
    private String title;
}
