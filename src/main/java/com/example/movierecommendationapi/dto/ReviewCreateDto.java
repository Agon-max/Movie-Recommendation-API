package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateDto {

    @NotNull(message = "movieId is required")
    private Long movieId;

    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    private String body;

    @Min(value = 1, message = "rating_score must be at least 1")
    @Max(value = 10, message = "rating_score must be at most 10")
    private int rating_score;
}
