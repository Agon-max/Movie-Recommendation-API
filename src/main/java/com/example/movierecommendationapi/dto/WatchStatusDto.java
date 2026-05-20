package com.example.movierecommendationapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchStatusDto {
    private boolean completed;
    private Integer watchedMinutes;
}
