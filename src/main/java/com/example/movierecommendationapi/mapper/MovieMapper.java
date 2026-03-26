package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { ActorMapper.class, DirectorMapper.class })
public interface MovieMapper {

    MovieDto toDto(Movie movie);

    Movie toEntity(MovieDto movieDto);

}
