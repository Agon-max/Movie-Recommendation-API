package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.WatchHistory;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.repository.UserRepository;
import com.example.movierecommendationapi.repository.WatchHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final MovieMapper movieMapper;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.api-key:}")
    private String openRouterApiKey;

    @Value("${openrouter.base-url:https://openrouter.ai/api/v1}")
    private String openRouterBaseUrl;

    public RecommendationService(
            MovieRepository movieRepository,
            UserRepository userRepository,
            WatchHistoryRepository watchHistoryRepository,
            MovieMapper movieMapper
    ) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.movieMapper = movieMapper;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // =========================
    // PUBLIC ENTRY POINT
    // =========================
    public List<MovieDto> getRecommendations(Long userId, int limit) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Movie> candidates = movieRepository.findTop500ByOrderByAverageRatingDesc();

        Set<String> aiSuggestions = getAISuggestions(user, limit);

        List<ScoredMovie> scored = new ArrayList<>();

        for (Movie movie : candidates) {

            double score = calculateScore(movie, user);

            if (aiSuggestions.contains(movie.getTitle())) {
                score += 5.0; // AI boost
            }

            scored.add(new ScoredMovie(movie, score));
        }

        return scored.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .map(sm -> movieMapper.toDto(sm.movie))
                .toList();
    }

    // =========================
    // SCORING ENGINE
    // =========================
    private double calculateScore(Movie movie, User user) {

        double score = 0;

        Set<Long> preferredGenres = getPreferredGenres(user);

        for (Genre genre : movie.getGenres()) {
            if (preferredGenres.contains(genre.getId())) {
                score += 3.0;
            }
        }

        if (watchedSimilar(user, movie)) {
            score += 2.5;
        }

        if (movie.getAverageRating() != null) {
            score += movie.getAverageRating() / 2.0;
        }

        return score;
    }

    private Set<Long> getPreferredGenres(User user) {

        return watchHistoryRepository
                .findByUserAndCompletedTrue(user)
                .stream()
                .flatMap(w -> w.getMovie().getGenres().stream())
                .map(Genre::getId)
                .collect(Collectors.toSet());
    }

    private boolean watchedSimilar(User user, Movie movie) {

        return watchHistoryRepository
                .findByUserAndCompletedTrue(user)
                .stream()
                .anyMatch(w ->
                        w.getMovie().getGenres().stream()
                                .anyMatch(g ->
                                        movie.getGenres().contains(g)
                                )
                );
    }

    // =========================
    // AI LAYER (CANDIDATE GENERATION)
    // =========================
    private Set<String> getAISuggestions(User user, int limit) {

        if (openRouterApiKey == null || openRouterApiKey.isEmpty()) {
            return Set.of();
        }

        try {

            String prompt =
                    "Return ONLY a JSON array of movie titles (no explanation). " +
                            "Base recommendations on user preferences and watch history. " +
                            "Limit: " + limit;

            String response = callOpenRouter(prompt);

            List<String> titles =
                    objectMapper.readValue(response, List.class);

            return new HashSet<>(titles);

        } catch (Exception e) {
            return Set.of();
        }
    }

    private String callOpenRouter(String prompt) {

        Map<String, Object> request = new HashMap<>();

        request.put("model", "openai/gpt-3.5-turbo");
        request.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        Map response = restTemplate.postForObject(
                openRouterBaseUrl + "/chat/completions",
                request,
                Map.class
        );

        List choices = (List) response.get("choices");
        Map message = (Map) ((Map) choices.get(0)).get("message");

        return (String) message.get("content");
    }

    // =========================
    // INTERNAL STRUCT    // =========================
    private static class ScoredMovie {
        Movie movie;
        double score;

        ScoredMovie(Movie movie, double score) {
            this.movie = movie;
            this.score = score;
        }
    }
}