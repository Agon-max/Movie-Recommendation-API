package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.RedemptionDto;
import com.example.movierecommendationapi.entity.Redemption;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RedemptionMapper {

    public RedemptionDto toDto(Redemption redemption);

    public Redemption toEntity(RedemptionDto redemptionDto);
}
