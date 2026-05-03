package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.GenreDto;
import com.example.movierecommendationapi.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    GenreDto toDto(Genre genre);
    Genre toEntity(GenreDto dto);
    @Mapping(target = "id", ignore = true)
    void updateManagedGenre(GenreDto genreDto, @MappingTarget Genre genre);
}
