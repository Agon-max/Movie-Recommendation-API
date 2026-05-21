package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.RedemptionDto;
import com.example.movierecommendationapi.entity.Redemption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RedemptionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "reward.id", target = "rewardId")
    @Mapping(source = "reward.name", target = "rewardName")
    @Mapping(source = "redeemedAt", target = "createdAt")
    @Mapping(target = "fulfilledAt", ignore = true)
    RedemptionDto toDto(Redemption redemption);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "reward", ignore = true)
    @Mapping(source = "createdAt", target = "redeemedAt")
    Redemption toEntity(RedemptionDto redemptionDto);
}
