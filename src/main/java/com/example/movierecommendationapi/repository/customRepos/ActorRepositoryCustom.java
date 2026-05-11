package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Actor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActorRepositoryCustom {
    Optional<Actor> findByTmdbId(Long tmdbId);
    public List<Actor> getActorsByMovie(Long movieId, String movieTitle);
}
