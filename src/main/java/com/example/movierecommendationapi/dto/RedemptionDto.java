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
    private LocalDateTime redeemedAt;
    private LocalDateTime fulfilledAt;
}
