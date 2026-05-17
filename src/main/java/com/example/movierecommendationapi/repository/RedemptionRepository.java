package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Redemption;
import com.example.movierecommendationapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, Long> {
    List<Redemption> findByUser(User user);
    boolean existsByUser(User user);
}
