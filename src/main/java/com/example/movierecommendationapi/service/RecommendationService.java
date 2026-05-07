package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final UserPreferencesService userPreferencesService;
    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.api-key:}")
    private String openRouterApiKey;

    @Value("${openrouter.base-url:https://openrouter.ai/api/v1}")
    private String openRouterBaseUrl;

    public RecommendationService(UserPreferencesService userPreferencesService, MovieRepository movieRepository) {
        this.userPreferencesService = userPreferencesService;
        this.movieRepository = movieRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<MovieDto> getRecommendations(Long userId, int count) {
        // Check if user has preferences
        var prefsOpt = userPreferencesService.getPreferencesByUserId(userId);

        if (prefsOpt.isEmpty()) {
            // No preferences: return popular movies
            System.out.println("No user preferences found. Returning popular movies.");
            return movieRepository.findAll().stream()
                    .limit(count)
                    .map(m -> new MovieDto()) // Map to DTO (implement based on your mapper)
                    .collect(Collectors.toList());
        }

        var prefs = prefsOpt.get();
        String prompt = buildPrompt(prefs, count);

        // Call OpenRouter AI
        if (openRouterApiKey == null || openRouterApiKey.isEmpty()) {
            System.out.println("OpenRouter API key not configured. Returning popular movies as fallback.");
            return movieRepository.findAll().stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }

        try {
            String aiResponse = callOpenRouter(prompt);
            List<String> movieTitles = parseAIResponse(aiResponse);
            return queryMoviesByTitles(movieTitles, count);
        } catch (Exception e) {
            System.out.println("Error calling OpenRouter: " + e.getMessage());
            return movieRepository.findAll().stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    private String buildPrompt(com.example.movierecommendationapi.dto.UserPreferencesDto prefs, int count) {
        return "Based on the following user preferences, recommend " + count + " movies. Return only movie titles separated by commas. " +
                "Survey: " + prefs.getSurveySummary() + ". " +
                "Watch history: " + prefs.getWatchHistorySummary() + ". " +
                "Return format: title1, title2, title3...";
    }

    private String callOpenRouter(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openRouterApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "openai/gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("max_tokens", 200);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        Map<String, Object> response = restTemplate.postForObject(
                openRouterBaseUrl + "/chat/completions",
                entity,
                Map.class
        );

        if (response == null || !response.containsKey("choices")) {
            throw new RuntimeException("Invalid OpenRouter response");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }

    private List<String> parseAIResponse(String response) {
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<MovieDto> queryMoviesByTitles(List<String> titles, int limit) {
        return titles.stream()
                .limit(limit)
                .map(title -> movieRepository.getMovieByTitle(title, org.springframework.data.domain.Pageable.unpaged())
                        .stream()
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
