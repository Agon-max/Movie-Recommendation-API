package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.UserPreferencesDto;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.UserPreferences;
import com.example.movierecommendationapi.mapper.UserMapper;
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
    private final UserService userService;
    private final UserMapper userMapper;

    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository, UserPreferencesMapper userPreferencesMapper, UserService userService, UserMapper userMapper) {
        this.userPreferencesRepository = userPreferencesRepository;
        this.userPreferencesMapper = userPreferencesMapper;
        this.objectMapper = new ObjectMapper();
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public Optional<UserPreferencesDto> getPreferencesByUserId(Long userId) {
        return userPreferencesRepository.findByUserId(userId)
                .map(userPreferencesMapper::toDto);
    }

    @Transactional
    public UserPreferencesDto createOrUpdatePreferences(Long userId, List<String> genres, List<String> actors, List<String> directors) {
        var userDto = userService.getUserById(userId);
        if(userDto == null) throw new IllegalArgumentException("User not found");
        User user = userMapper.toEntity(userDto);
        UserPreferences prefs = userPreferencesRepository.findByUserId(userId)
                .orElse(new UserPreferences());
        prefs.setUser(user);
        prefs.setFavoriteGenres(genres);
        prefs.setFavoriteActors(actors);
        prefs.setFavoriteDirectors(directors);
        prefs.setSurveySummary("Likes " + String.join(", ", genres) + ". Favorite actors: " + String.join(", ", actors));
        return userPreferencesMapper.toDto(userPreferencesRepository.save(prefs));
    }

//    private String listToJson(List<String> list) {
//        try {
//            return objectMapper.writeValueAsString(list);
//        } catch (Exception e) {
//            return "[]";
//        }
//    }
}
