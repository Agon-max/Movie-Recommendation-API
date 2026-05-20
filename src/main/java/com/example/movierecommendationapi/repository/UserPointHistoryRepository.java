package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.UserPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPointHistoryRepository extends JpaRepository<UserPointHistory, Long> {
    List<UserPointHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
