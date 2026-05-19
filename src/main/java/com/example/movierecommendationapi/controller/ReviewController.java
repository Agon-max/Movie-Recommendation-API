package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.ReviewCreateDto;
import com.example.movierecommendationapi.dto.ReviewDto;
import com.example.movierecommendationapi.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review Controller", description = "Review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Create a review for a movie")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewCreateDto dto) {
        ReviewDto created = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Get all reviews (paginated)")
    public Page<ReviewDto> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return reviewService.getAllReviews(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review by id")
    public ResponseEntity<ReviewDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Get reviews for a movie")
    public ResponseEntity<List<ReviewDto>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovieId(movieId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update one of your own reviews")
    public ResponseEntity<ReviewDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCreateDto dto
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete one of your own reviews")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
