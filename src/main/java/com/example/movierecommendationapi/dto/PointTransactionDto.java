package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.entity.enums.PointTransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PointTransactionDto {

    private Long id;
    private Long userId;
    private int points;
    private PointTransactionType type;
    private PointEventType sourceEventType;
    private Long sourceId;
    private String description;
    private LocalDateTime createdAt;
}
