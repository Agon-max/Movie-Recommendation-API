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
        // entityManager.find() searches by primary key (id), not by tmdbId.
        // We need a JPQL query against the actual tmdbId column.
        var results = entityManager.createQuery(
                        "SELECT m FROM Movie m WHERE m.tmdbId = :tmdbId",
                        Movie.class)
                .setParameter("tmdbId", tmdbId)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }
}
