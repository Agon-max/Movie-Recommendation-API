package com.example.movierecommendationapi.dto;

import com.example.movierecommendationapi.entity.enums.RewardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateRewardDto {

    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "pointCost is required")
    @Min(value = 0, message = "pointCost must be >= 0")
    private Integer pointCost;

    @NotNull(message = "type is required")
    private RewardType type;

    @PositiveOrZero(message = "monetaryValue must be >= 0")
    private BigDecimal monetaryValue;

    private boolean active = true;

    @PositiveOrZero(message = "stock must be >= 0")
    private int stock;
}
