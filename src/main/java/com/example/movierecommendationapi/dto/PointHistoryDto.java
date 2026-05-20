package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryDto {
    private Long id;
    private Long userId;
    private PointEventType eventType;
    private int pointsReceived;
    private LocalDateTime createdAt;
}
