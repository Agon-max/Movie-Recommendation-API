package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.RedemptionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RedemptionDto {

    private Long id;
    private Long userId;
    private Long rewardId;
    private String rewardName;
    private int pointsSpent;
    private RedemptionStatus status;
    // The frontend uses `createdAt` for the timestamp; map from
    // Redemption.redeemedAt in RedemptionMapper.
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;
}
