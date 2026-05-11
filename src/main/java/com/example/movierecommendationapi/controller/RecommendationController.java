package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.service.RecommendationService;
import com.example.movierecommendationapi.service.UserSurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendation Controller", description = "Recommendation management endpoints")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserSurveyService userSurveyService;

    public RecommendationController(RecommendationService recommendationService, UserSurveyService userSurveyService) {
        this.recommendationService = recommendationService;
        this.userSurveyService = userSurveyService;
    }

    @PostMapping("/survey/{userId}")
    @Operation(summary = "Submit user survey for initial preferences")
    public ResponseEntity<UserSurveyDto> submitSurvey(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> surveyData) {
        List<String> genres = (List<String>) surveyData.get("favoriteGenres");
        List<String> actors = (List<String>) surveyData.get("favoriteActors");
        List<String> directors = (List<String>) surveyData.get("favoriteDirectors");
        String dislikes = (String) surveyData.get("dislikes");

        UserSurveyDto result = userSurveyService.submitSurvey(userId, genres, actors, directors, dislikes);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get AI-powered movie recommendations for a user")
    public ResponseEntity<List<MovieDto>> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int count) {
        List<MovieDto> recommendations = recommendationService.getRecommendations(userId, count);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/survey/{userId}")
    @Operation(summary = "Get user survey if completed")
    public ResponseEntity<?> getUserSurvey(@PathVariable Long userId) {
        var survey = userSurveyService.getSurveyByUserId(userId);
        if (survey.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No survey found. Please complete the survey first."));
        }
        return ResponseEntity.ok(survey.get());
    }
}
