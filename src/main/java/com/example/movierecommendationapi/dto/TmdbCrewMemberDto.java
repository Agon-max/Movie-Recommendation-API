package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbCrewMemberDto {
    private Long id;
    private String name;
    private String job;
    private String department;
}
