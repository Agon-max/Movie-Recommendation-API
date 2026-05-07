// UserSurveyRepository.java
package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.UserSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSurveyRepository extends JpaRepository<UserSurvey, Long> {
    Optional<UserSurvey> findByUserId(Long userId);
}