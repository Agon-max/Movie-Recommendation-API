package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.GenreDto;
import com.example.movierecommendationapi.dto.TmdbGenreDto;
import com.example.movierecommendationapi.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    @Mapping(source = "name", target = "title")
    GenreDto toDto(Genre genre);

    @Mapping(source = "title", target = "name")
    Genre toEntity(GenreDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "title", target = "name")
    void updateManagedGenre(GenreDto genreDto, @MappingTarget Genre genre);
}
