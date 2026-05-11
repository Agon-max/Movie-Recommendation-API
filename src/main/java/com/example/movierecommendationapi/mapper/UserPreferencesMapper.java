// UserPreferencesMapper.java
package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.UserPreferencesDto;
import com.example.movierecommendationapi.entity.UserPreferences;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPreferencesMapper {
    @Mapping(source = "user.id", target = "userId")
    UserPreferencesDto toDto(UserPreferences userPreferences);
    @Mapping(target = "user", ignore = true)
    UserPreferences toEntity(UserPreferencesDto userPreferencesDto);
}