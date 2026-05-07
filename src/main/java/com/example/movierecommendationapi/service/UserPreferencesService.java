package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.UserPreferencesDto;
import com.example.movierecommendationapi.entity.UserPreferences;
import com.example.movierecommendationapi.mapper.UserPreferencesMapper;
import com.example.movierecommendationapi.repository.UserPreferencesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserPreferencesService {
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserPreferencesMapper userPreferencesMapper;
    private final ObjectMapper objectMapper;

    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository, UserPreferencesMapper userPreferencesMapper) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.userPreferencesMapper = userPreferencesMapper;
        this.objectMapper = new ObjectMapper();
    }

    public Optional<UserPreferencesDto> getPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findByUserId(userId)
                .map(userPreferencesMapper::toDto);
    }

    @Transactional
    public UserPreferencesDto createOrUpdatePreferences(Long userId, List<String> genres, List<String> actors, List<String> directors) {
        UserPreferences prefs = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());
        prefs.setUserId(userId);
        prefs.setFavoriteGenres(listToJson(genres));
        prefs.setFavoriteActors(listToJson(actors));
        prefs.setFavoriteDirectors(listToJson(directors));
        prefs.setSurveySummary("Likes " + String.join(", ", genres) + ". Favorite actors: " + String.join(", ", actors));
        return userPreferencesMapper.toDto(userPreferencesRepository.save(prefs));
    }

    private String listToJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
}
