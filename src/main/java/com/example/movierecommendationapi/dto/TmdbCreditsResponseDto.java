package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TmdbCreditsResponseDto {
    private Long id;
    private List<TmdbActorDto> cast;
    private List<TmdbCrewMemberDto> crew;
}
