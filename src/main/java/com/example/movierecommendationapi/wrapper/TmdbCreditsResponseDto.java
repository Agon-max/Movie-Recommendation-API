package com.example.movierecommendationapi.wrapper;

import com.example.movierecommendationapi.dto.TmdbCastMemberDto;
import com.example.movierecommendationapi.dto.TmdbCrewMemberDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TmdbCreditsResponseDto {
    private Long id;
    private List<TmdbCastMemberDto> cast;
    private List<TmdbCrewMemberDto> crew;
}
