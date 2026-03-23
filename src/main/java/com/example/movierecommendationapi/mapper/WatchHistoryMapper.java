package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.WatchHistoryDto;
import com.example.movierecommendationapi.entity.WatchHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WatchHistoryMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "movie.id", target = "movieId")
    WatchHistoryDto toDto(WatchHistory watchHistory);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    WatchHistory toEntity(WatchHistoryDto watchHistoryDto);

}
