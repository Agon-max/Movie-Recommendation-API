package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { ActorMapper.class, DirectorMapper.class })
public interface MovieMapper {
    MovieDto toDto(Movie movie);

    Movie toEntity(MovieDto movieDto);

    /**
     * Maps DTO into an existing entity without creating a new object.
     *
     * Updates a managed entity so that JPA dirty checking can persist changes.
     *
     * @param movieDto the DTO object containing updated values
     * @param movie the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    void updateMovieFromDto(MovieDto movieDto, @MappingTarget Movie movie);
}
