package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.repository.customRepos.MovieRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;
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

    @Override
    public Page<Movie> searchMovies(
            String title,
            Long genreId,
            Integer releaseYear,
            Pageable pageable
    ) {
        // Build the WHERE clause and params once, reuse for both the data
        // query and the count query so they stay in sync. Adult content is
        // always excluded — this is the choke point every browse query funnels
        // through, so the UI never has a chance to surface it.
        StringBuilder where = new StringBuilder(" WHERE m.adult = false");
        Map<String, Object> params = new HashMap<>();

        boolean hasTitle = title != null && !title.isBlank();
        if (hasTitle) {
            where.append(" AND LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))");
            params.put("title", title.trim());
        }
        if (genreId != null) {
            where.append(" AND g.id = :genreId");
            params.put("genreId", genreId);
        }
        if (releaseYear != null) {
            // releaseDate is stored as a "YYYY-MM-DD" string, so LIKE
            // works across all JPA dialects without dialect-specific
            // SUBSTRING/EXTRACT calls.
            where.append(" AND m.releaseDate LIKE :releaseYearPrefix");
            params.put("releaseYearPrefix", releaseYear + "%");
        }

        String join = genreId != null ? " JOIN m.genres g" : "";

        // Build ORDER BY from Pageable's Sort so the controller's
        // @PageableDefault(sort = "title") keeps working.
        StringBuilder orderBy = new StringBuilder();
        if (pageable.getSort().isSorted()) {
            orderBy.append(" ORDER BY ");
            boolean first = true;
            for (Sort.Order o : pageable.getSort()) {
                if (!first) orderBy.append(", ");
                orderBy.append("m.").append(o.getProperty()).append(' ').append(o.getDirection().name());
                first = false;
            }
        }

        String selectJpql = "SELECT DISTINCT m FROM Movie m" + join + where + orderBy;
        String countJpql  = "SELECT COUNT(DISTINCT m) FROM Movie m" + join + where;

        TypedQuery<Movie> query = entityManager.createQuery(selectJpql, Movie.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        params.forEach((k, v) -> {
            query.setParameter(k, v);
            countQuery.setParameter(k, v);
        });

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = countQuery.getSingleResult();
        return new PageImpl<>(query.getResultList(), pageable, total != null ? total : 0L);
    }
}
