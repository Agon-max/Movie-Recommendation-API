package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RatingDto {

    private Long id;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
    private Long userId;
    private Long movieId;
}
