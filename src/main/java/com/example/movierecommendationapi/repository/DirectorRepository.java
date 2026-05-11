package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.repository.customRepos.DirectorRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long>, DirectorRepositoryCustom {
    @Transactional
    @Query(value = """
    INSERT INTO directors (tmdb_id, name)
    VALUES (:tmdbId, :name)
    ON CONFLICT (tmdb_id)
    DO UPDATE SET name = EXCLUDED.name
    RETURNING id, tmdb_id, name
""", nativeQuery = true)
    Object upsertDirector(
            @Param("tmdbId") Long tmdbId,
            @Param("name") String name
    );
}
