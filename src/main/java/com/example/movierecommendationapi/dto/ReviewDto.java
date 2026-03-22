package com.example.movierecommendationapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {

    private Long id;
    private Long userId;
    private Long movieId;
    private String title;
    private String body;
    private boolean containsSpoilers;
    private int helpfulVotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean pointsAwarded;
}
