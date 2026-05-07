package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.entity.UserSurvey;
import com.example.movierecommendationapi.mapper.UserSurveyMapper;
import com.example.movierecommendationapi.repository.UserSurveyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserSurveyService {
    private final UserSurveyRepository userSurveyRepository;
    private final UserSurveyMapper userSurveyMapper;
    private final UserPreferencesService userPreferencesService;
    private final ObjectMapper objectMapper;

    public UserSurveyService(UserSurveyRepository userSurveyRepository, UserSurveyMapper userSurveyMapper, UserPreferencesService userPreferencesService) {
        this.userSurveyRepository = userSurveyRepository;
        this.userSurveyMapper = userSurveyMapper;
        this.userPreferencesService = userPreferencesService;
        this.objectMapper = new ObjectMapper();
    }

    public Optional<UserSurveyDto> getSurveyByUserId(Long userId) {
        return userSurveyRepository.findByUserId(userId)
                .map(userSurveyMapper::toDto);
    }

    @Transactional
    public UserSurveyDto submitSurvey(Long userId, List<String> genres, List<String> actors, List<String> directors, String dislikes) {
        UserSurvey survey = new UserSurvey();
        survey.setUserId(userId);
        survey.setFavoriteGenres(listToJson(genres));
        survey.setFavoriteActors(listToJson(actors));
        survey.setFavoriteDirectors(listToJson(directors));
        survey.setDislikes(dislikes);

        UserSurvey savedSurvey = userSurveyRepository.save(survey);

        // Update UserPreferences based on survey
        userPreferencesService.createOrUpdatePreferences(userId, genres, actors, directors);

        return userSurveyMapper.toDto(savedSurvey);
    }

    private String listToJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
}
