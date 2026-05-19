package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.ReviewCreateDto;
import com.example.movierecommendationapi.dto.ReviewDto;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.entity.Review;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.ReviewMapper;
import com.example.movierecommendationapi.repository.ReviewRepository;
import com.example.movierecommendationapi.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final MovieService movieService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final PointService pointService;

    public ReviewService(MovieService movieService,
                         UserService userService,
                         ReviewRepository reviewRepository,
                         ReviewMapper reviewMapper,
                         PointService pointService) {
        this.movieService = movieService;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.pointService = pointService;
    }

    @Transactional
    public ReviewDto createReview(ReviewCreateDto dto) {
        if (dto == null || dto.getMovieId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "movieId is required");
        }

        User currentUser = requireCurrentUser();
        Movie movie = movieService.getMovieEntityById(dto.getMovieId());

        if (reviewRepository.existsByUserIdAndMovieId(currentUser.getId(), movie.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already reviewed this movie"
            );
        }

        Review review = new Review();
        review.setUser(currentUser);
        review.setMovie(movie);
        review.setTitle(dto.getTitle());
        review.setBody(dto.getBody());
        review.setRating_score(dto.getRating_score());

        Review saved = reviewRepository.save(review);

        if (!saved.isPointsAwarded()) {
            pointService.awardPoints(currentUser, PointEventType.WRITE_REVIEW);
            saved.setPointsAwarded(true);
        }

        return reviewMapper.toDto(saved);
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Review not found"));
        return reviewMapper.toDto(review);
    }

    @Transactional
    public ReviewDto updateReview(Long id, ReviewCreateDto dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Review not found"));

        User currentUser = requireCurrentUser();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this review"
            );
        }

        if (dto.getTitle() != null) {
            review.setTitle(dto.getTitle());
        }
        if (dto.getBody() != null) {
            review.setBody(dto.getBody());
        }
        if (dto.getRating_score() > 0) {
            review.setRating_score(dto.getRating_score());
        }
        if (dto.getMovieId() != null && !dto.getMovieId().equals(review.getMovie().getId())) {
            review.setMovie(movieService.getMovieEntityById(dto.getMovieId()));
        }

        return reviewMapper.toDto(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Review not found"));

        User currentUser = requireCurrentUser();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this review"
            );
        }

        reviewRepository.delete(review);
    }

    public Page<ReviewDto> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(reviewMapper::toDto);
    }

    public List<ReviewDto> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public Page<ReviewDto> getReviewsByMovieId(Long movieId, Pageable pageable) {
        return reviewRepository.findByMovieId(movieId, pageable).map(reviewMapper::toDto);
    }

    private User requireCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        return userService.getUserByEntityId(details.getId());
    }
}
