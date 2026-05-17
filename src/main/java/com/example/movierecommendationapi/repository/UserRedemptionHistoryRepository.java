package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.UserRedemptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedemptionHistoryRepository extends JpaRepository<UserRedemptionHistory, Long> {
}
