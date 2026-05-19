package com.example.movierecommendationapi.mapper;

import com.example.movierecommendationapi.dto.ReviewCreateDto;
import com.example.movierecommendationapi.dto.ReviewDto;
import com.example.movierecommendationapi.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "movie.id", target = "movieId")
    ReviewDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pointsAwarded", ignore = true)
    Review toEntity(ReviewCreateDto dto);
}
