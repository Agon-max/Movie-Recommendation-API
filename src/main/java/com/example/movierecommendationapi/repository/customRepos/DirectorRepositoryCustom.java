package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepositoryCustom {
    Optional<Director> findByTmdbId(Long tmdbId);
    List<Director> getDirectorsByMovie(Long movieId, String movieTitle);
}
