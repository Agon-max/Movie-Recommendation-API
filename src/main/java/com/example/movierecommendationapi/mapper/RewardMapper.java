package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.CreateRewardDto;
import com.example.movierecommendationapi.dto.RewardDto;
import com.example.movierecommendationapi.entity.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RewardMapper {

    RewardDto toDto(Reward reward);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRedemptionHistory", ignore = true)
    @Mapping(target = "redemptions", ignore = true)
    Reward toEntity(RewardDto rewardDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRedemptionHistory", ignore = true)
    @Mapping(target = "redemptions", ignore = true)
    Reward toEntity(CreateRewardDto dto);
}
