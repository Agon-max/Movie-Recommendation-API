package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.PointTransactionDto;
import com.example.movierecommendationapi.entity.PointTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PointTransactionMapper {

    public PointTransactionDto toDto(PointTransaction pointTransaction);

    public PointTransaction toEntity(PointTransactionDto pointTransactionDto);
}
