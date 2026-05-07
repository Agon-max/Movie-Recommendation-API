package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepositoryCustom {
    public Optional<Genre> findByTmdbId(Long tmdbId);
}
