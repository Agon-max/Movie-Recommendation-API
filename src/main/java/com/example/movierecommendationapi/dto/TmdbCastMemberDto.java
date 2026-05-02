package com.example.movierecommendationapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmdbCastMemberDto {
    private Long id;
    private String name;

    @JsonProperty("known_for_department")
    private String knownForDepartment;
}
