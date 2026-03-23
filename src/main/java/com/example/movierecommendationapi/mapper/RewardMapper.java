package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.RewardDto;
import com.example.movierecommendationapi.entity.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RewardMapper {

    public RewardDto toDto(Reward reward);

    public Reward toEntity(RewardDto rewardDto);
}
