package com.example.movierecommendationapi.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectorDto {
    @Id
    @GeneratedValue()
    private Long id;
}
