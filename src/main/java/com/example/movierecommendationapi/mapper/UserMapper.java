package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.UserDto;
import com.example.movierecommendationapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
