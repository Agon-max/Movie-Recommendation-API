package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.ReviewDto;
import com.example.movierecommendationapi.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller", description = "Review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Create a review")
    public ResponseEntity<ReviewDto> createReview(ReviewDto dto){
        return ResponseEntity.ok(reviewService.createReview(dto));
    }
}
