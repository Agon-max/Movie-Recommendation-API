package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.RewardType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RewardDto {

    private Long id;
    private String name;
    private String description;
    private int pointCost;
    private RewardType type;
    private BigDecimal monetaryValue;
    private int stock;
    private boolean active;
}
