package com.example.movierecommendationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchMovieResponse {
    private int pointsAwarded;
    private int totalPoints;
    private boolean alreadyCompleted;
    private boolean completedNow;
    private double watchedPercentage;
}
