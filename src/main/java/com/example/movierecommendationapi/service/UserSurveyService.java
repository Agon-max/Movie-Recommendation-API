package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.UserDto;
import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.UserSurvey;
import com.example.movierecommendationapi.mapper.UserMapper;
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
    private final UserService userService;
    private final UserMapper userMapper;

    public UserSurveyService(UserSurveyRepository userSurveyRepository, UserSurveyMapper userSurveyMapper, UserPreferencesService userPreferencesService, UserService userService, UserMapper userMapper) {
        this.userSurveyRepository = userSurveyRepository;
        this.userSurveyMapper = userSurveyMapper;
        this.userPreferencesService = userPreferencesService;
        this.objectMapper = new ObjectMapper();
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Optional<UserSurveyDto> getSurveyByUserId(Long userId) {
        return userSurveyRepository.findByUserId(userId)
                .map(userSurveyMapper::toDto);
    }

    @Transactional
    public UserSurveyDto submitSurvey(Long userId, List<String> genres, List<String> actors, List<String> directors, String dislikes) {
        UserDto userDto = userService.getUserById(userId);
        if(userDto == null) throw new IllegalArgumentException("User not found");
        User user = userMapper.toEntity(userDto);
        UserSurvey survey = new UserSurvey();
        survey.setUser(user);
        survey.setFavoriteGenres(genres);
        survey.setFavoriteActors(actors);
        survey.setFavoriteDirectors(directors);
        survey.setDislikes(dislikes);

        UserSurvey savedSurvey = userSurveyRepository.save(survey);

        // Update UserPreferences based on the survey
        userPreferencesService.createOrUpdatePreferences(userId, genres, actors, directors);

        return userSurveyMapper.toDto(savedSurvey);
    }

//    private String listToJson(List<String> list) {
//        try {
//            return objectMapper.writeValueAsString(list);
//        } catch (Exception e) {
//            return "[]";
//        }
//    }
}
