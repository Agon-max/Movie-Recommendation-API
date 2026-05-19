package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);

    Page<Review> findByMovieId(Long movieId, Pageable pageable);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
