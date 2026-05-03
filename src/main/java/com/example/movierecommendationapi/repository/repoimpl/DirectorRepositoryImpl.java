package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.repository.customRepos.DirectorRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class DirectorRepositoryImpl implements DirectorRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Director> getDirectorsByMovie(Long movieId, String movieTitle) {

        StringBuilder jpql = new StringBuilder(
                "SELECT d FROM Director d JOIN d.movies m WHERE 1=1"
        );

        if (movieId != null) {
            jpql.append(" AND m.id = :movieId");
        }

        if (movieTitle != null && !movieTitle.isBlank()) {
            jpql.append(" AND LOWER(m.title) LIKE LOWER(:movieTitle)");
        }

        var query = entityManager.createQuery(jpql.toString(), Director.class);

        if (movieId != null) {
            query.setParameter("movieId", movieId);
        }

        if (movieTitle != null && !movieTitle.isBlank()) {
            query.setParameter("movieTitle", "%" + movieTitle + "%");
        }

        return query.getResultList();
    }
}
