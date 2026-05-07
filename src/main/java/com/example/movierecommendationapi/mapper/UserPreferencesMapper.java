// UserPreferencesMapper.java
package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.UserPreferencesDto;
import com.example.movierecommendationapi.entity.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPreferencesMapper {
    UserPreferencesDto toDto(UserPreferences userPreferences);
    UserPreferences toEntity(UserPreferencesDto userPreferencesDto);
}