package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    Optional<WatchHistory> findByUserAndMovie(User user, Movie movie);
    List<WatchHistory> findByUserAndCompletedTrue(User user);
}
