package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.ReviewDto;
import com.example.movierecommendationapi.entity.Review;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.mapper.ReviewMapper;
import com.example.movierecommendationapi.mapper.UserMapper;
import com.example.movierecommendationapi.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final MovieService movieService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(MovieService movieService, UserService userService, UserMapper userMapper, MovieMapper movieMapper, ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.movieService = movieService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    public ReviewDto createReview(ReviewDto reviewDto)
    {
        var review = new Review();
        if(reviewDto.getMovieId() == null || reviewDto.getUserId() == null) {
            throw new IllegalArgumentException("Movie ID and User ID cannot be null!");
        }
        var movieToSet = movieService.getMovieById(reviewDto.getMovieId());
        var userToSet = userService.getUserById(reviewDto.getUserId());
        if(movieToSet == null){
            throw new IllegalArgumentException("Movie cannot be null!");
        }
        if(userToSet == null){
            throw new IllegalArgumentException("User cannot be null!");
        }

        review.setMovie(movieMapper.toEntity(movieToSet));
        review.setUser(userMapper.toEntity(userToSet));
        review.setTitle(reviewDto.getTitle());
        review.setRating_score(reviewDto.getRating_score());
        review.setBody(reviewDto.getBody());

        reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }
}
