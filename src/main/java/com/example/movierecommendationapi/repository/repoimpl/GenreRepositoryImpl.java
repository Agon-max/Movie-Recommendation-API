package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.repository.customRepos.GenreRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

public class GenreRepositoryImpl implements GenreRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<Genre> findByTmdbId(Long tmdbId) {
        List<Genre> result = entityManager.createQuery(
                        "SELECT g FROM Genre g WHERE g.tmdbId = :tmdbId",
                        Genre.class
                )
                .setParameter("tmdbId", tmdbId)
                .getResultList();

        return result.stream().findFirst();
    }
}
