package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.DirectorDto;
import com.example.movierecommendationapi.entity.Director;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DirectorMapper {

    Director toEntity(DirectorDto dto);

    DirectorDto toDto(Director director);

    List<Director> toEntityList(List<DirectorDto> dtos);

    List<DirectorDto> toDtoList(List<Director> directors);
}
