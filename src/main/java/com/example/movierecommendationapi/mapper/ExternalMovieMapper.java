package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.ExternalMovieDto;
import com.example.movierecommendationapi.entity.ExternalMovie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExternalMovieMapper {

    @Mapping(source = "movie.id", target = "movieId")
    ExternalMovieDto toDto(ExternalMovie externalMovie);

    @Mapping(target = "movie", ignore = true)
    ExternalMovie toEntity(ExternalMovieDto externalMovieDto);

}
