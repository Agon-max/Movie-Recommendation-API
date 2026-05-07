package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.repository.customRepos.ActorRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

public class ActorRepositoryImpl implements ActorRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Actor> getActorsByMovie(Long movieId, String movieTitle) {

        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Actor a JOIN a.movies m WHERE 1=1"
        );

        if (movieId != null) {
            jpql.append(" AND m.id = :movieId");
        }

        if (movieTitle != null && !movieTitle.isBlank()) {
            jpql.append(" AND LOWER(m.title) LIKE LOWER(:movieTitle)");
        }

        var query = entityManager.createQuery(jpql.toString(), Actor.class);

        if (movieId != null) {
            query.setParameter("movieId", movieId);
        }

        if (movieTitle != null && !movieTitle.isBlank()) {
            query.setParameter("movieTitle", "%" + movieTitle + "%");
        }

        return query.getResultList();
    }

    @Override
    public Optional<Actor> findByTmdbId(Long tmdbId) {
        Actor actor = entityManager.find(Actor.class, tmdbId);  // Or query by tmdbId
        return Optional.ofNullable(actor);
    }
}
