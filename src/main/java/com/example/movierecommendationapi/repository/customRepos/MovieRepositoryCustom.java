package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MovieRepositoryCustom {
    Optional<Movie> findByTmdbId(Long tmdbId);

    Page<Movie> searchMovies(
            String title,
            Long genreId,
            Integer releaseYear,
            Pageable pageable
    );
}
