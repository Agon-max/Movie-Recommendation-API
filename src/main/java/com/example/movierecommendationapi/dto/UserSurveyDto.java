// UserSurveyDto.java
package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserSurveyDto {
    private Long id;
    private Long userId;
    private List<String> favoriteGenres;
    private List<String> favoriteActors;
    private List<String> favoriteDirectors;
    private String dislikes;
    private LocalDateTime completedAt;
}