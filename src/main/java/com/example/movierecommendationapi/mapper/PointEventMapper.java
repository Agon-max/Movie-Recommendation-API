package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.PointEventDto;
import com.example.movierecommendationapi.entity.PointEvent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PointEventMapper {

    PointEventDto toDto(PointEvent pointEvent);

    PointEvent toEntity(PointEventDto pointEventDto);

}
