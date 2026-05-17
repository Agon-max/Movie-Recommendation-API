package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSurveyRequest {

    private List<String> favoriteGenres;
    private List<String> favoriteActors;
    private List<String> favoriteDirectors;
    private String dislikes;
}