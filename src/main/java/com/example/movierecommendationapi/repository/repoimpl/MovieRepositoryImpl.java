package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.repository.customRepos.MovieRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class MovieRepositoryImpl implements MovieRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    public MovieRepositoryImpl() {

    }

    @Override
    public Optional<Movie> findByTmdbId(Long tmdbId) {
        var movie = entityManager.find(Movie.class, tmdbId);
        return Optional.ofNullable(movie);
    }
}
