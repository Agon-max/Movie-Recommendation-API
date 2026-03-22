package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointEventDto {

    private Long id;
    private PointEventType eventType;
    private int pointsAwarded;
    private String description;
    private boolean active;
}
