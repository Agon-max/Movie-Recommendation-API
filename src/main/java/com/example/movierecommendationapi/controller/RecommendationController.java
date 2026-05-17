package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.dto.UserSurveyRequest;
import com.example.movierecommendationapi.dto.SurveyResponse;
import com.example.movierecommendationapi.service.RecommendationService;
import com.example.movierecommendationapi.service.UserSurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Recommendation Controller", description = "User survey and recommendation endpoints")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserSurveyService userSurveyService;

    public RecommendationController(
            RecommendationService recommendationService,
            UserSurveyService userSurveyService
    ) {
        this.recommendationService = recommendationService;
        this.userSurveyService = userSurveyService;
    }

    @PostMapping("/{userId}/survey")
    @Operation(summary = "Submit user survey for initial preferences")
    public ResponseEntity<UserSurveyDto> submitSurvey(
            @PathVariable Long userId,
            @RequestBody UserSurveyRequest request
    ) {
        UserSurveyDto result = userSurveyService.submitSurvey(
                userId,
                request.getFavoriteGenres(),
                request.getFavoriteActors(),
                request.getFavoriteDirectors(),
                request.getDislikes()
        );

        return ResponseEntity.ok(result);
    }


    @GetMapping("/{userId}/survey")
    @Operation(summary = "Get user survey if completed")
    public ResponseEntity<SurveyResponse> getUserSurvey(
            @PathVariable Long userId
    ) {
        var survey = userSurveyService.getSurveyByUserId(userId);

        if (survey.isEmpty()) {
            return ResponseEntity.ok(
                    new SurveyResponse(
                            false,
                            null,
                            "No survey found. Please complete the survey first."
                    )
            );
        }

        return ResponseEntity.ok(
                new SurveyResponse(
                        true,
                        survey.get(),
                        null
                )
        );
    }


    @GetMapping("/{userId}/recommendations")
    @Operation(summary = "Get AI-powered movie recommendations for a user")
    public ResponseEntity<List<MovieDto>> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int count
    ) {
        if (count <= 0 || count > 50) {
            return ResponseEntity.badRequest().build();
        }

        List<MovieDto> recommendations =
                recommendationService.getRecommendations(userId, count);

        return ResponseEntity.ok(recommendations);
    }
}