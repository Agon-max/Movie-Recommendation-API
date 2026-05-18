package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.PointEvent;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.repository.customRepos.PointEventRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointEventRepository extends JpaRepository<PointEvent, Long>, PointEventRepositoryCustom {
    Optional<PointEvent> findByEventType(PointEventType eventType);
    boolean existsByUserIdAndEventType(Long id, PointEventType pointEventType);

}
