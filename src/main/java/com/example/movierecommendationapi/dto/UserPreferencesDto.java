package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserPreferencesDto {
    private Long id;
    private Long userId;
    private List<String> favoriteGenres;
    private List<String> favoriteActors;
    private List<String> favoriteDirectors;
    private String watchHistorySummary;
    private String surveySummary;
    private Integer totalMoviesWatched;
    private LocalDateTime lastUpdated;
}