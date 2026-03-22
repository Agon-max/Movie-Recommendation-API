package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WatchHistoryDto {

    private Long id;
    private Long userId;
    private Long movieId;
    private LocalDateTime watchedAt;
    private boolean pointsAwarded;
}
