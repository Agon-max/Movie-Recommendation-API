package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Movie;

import java.util.Optional;

public interface MovieRepositoryCustom {
    public Optional<Movie> findByTmdbId(Long tmdbId);
}
