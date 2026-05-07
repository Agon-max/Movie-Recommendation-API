// UserSurveyMapper.java
package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.entity.UserSurvey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSurveyMapper {
    UserSurveyDto toDto(UserSurvey userSurvey);
    UserSurvey toEntity(UserSurveyDto userSurveyDto);
}