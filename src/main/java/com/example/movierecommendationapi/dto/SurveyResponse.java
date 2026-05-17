package com.example.movierecommendationapi.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResponse {
    private boolean exists;
    private UserSurveyDto survey;
    private String message;
}
