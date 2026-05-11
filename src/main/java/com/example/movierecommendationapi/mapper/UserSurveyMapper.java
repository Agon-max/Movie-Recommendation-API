// UserSurveyMapper.java
package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.UserSurveyDto;
import com.example.movierecommendationapi.entity.UserSurvey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSurveyMapper {
    @Mapping(source = "user.id", target = "userId")
    UserSurveyDto toDto(UserSurvey userSurvey);
    @Mapping(target = "user", ignore = true)
    UserSurvey toEntity(UserSurveyDto userSurveyDto);
}