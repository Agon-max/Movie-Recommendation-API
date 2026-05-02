package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreDto {
    private Long id;
    @NotBlank
    private String title;
}
