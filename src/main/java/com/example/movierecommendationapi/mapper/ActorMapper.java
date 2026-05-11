package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.entity.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActorMapper {

    @Mapping(target = "movies", ignore = true)
    Actor toEntity(ActorDto dto);
    ActorDto toDto(Actor actor);
    List<Actor> toEntityList(List<ActorDto> dtos);
    List<ActorDto> toDtoList(List<Actor> actors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movies", ignore = true)
    void updateManagedActor(ActorDto actorDto, @MappingTarget Actor actor );
}