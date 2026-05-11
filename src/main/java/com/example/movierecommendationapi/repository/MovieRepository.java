package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.repository.customRepos.MovieRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository <Movie, Long>, MovieRepositoryCustom {

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title ,'%'))")
    public Page<Movie> getMovieByTitle(@Param("title") String title, Pageable pageable);
    @Transactional
    @Query(value = """
    INSERT INTO movies (
        tmdb_id,
        title,
        overview,
        release_date,
        language,
        average_rating
    )
    VALUES (
        :tmdbId,
        :title,
        :overview,
        :releaseDate,
        :language,
        :averageRating
    )
    ON CONFLICT (tmdb_id)
    DO UPDATE SET
        title = EXCLUDED.title,
        overview = EXCLUDED.overview,
        release_date = EXCLUDED.release_date,
        language = EXCLUDED.language,
        average_rating = EXCLUDED.average_rating
    RETURNING id, tmdb_id, title
""", nativeQuery = true)
    Object upsertMovie(
            @Param("tmdbId") Long tmdbId,
            @Param("title") String title,
            @Param("overview") String overview,
            @Param("releaseDate") String release_date,
            @Param("language") String language,
            @Param("averageRating") Double average_rating
    );
}
