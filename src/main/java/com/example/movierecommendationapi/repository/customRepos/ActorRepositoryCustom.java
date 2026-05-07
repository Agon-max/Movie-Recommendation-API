package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorRepositoryCustom {
    Optional<Actor> findByTmdbId(Long tmdbId);
    public List<Actor> getActorsByMovie(Long movieId, String movieTitle);
}
