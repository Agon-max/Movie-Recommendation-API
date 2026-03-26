package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.entity.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    Actor toEntity(ActorDto dto);

    ActorDto toDto(Actor actor);

    List<Actor> toEntityList(List<ActorDto> dtos);

    List<ActorDto> toDtoList(List<Actor> actors);
}