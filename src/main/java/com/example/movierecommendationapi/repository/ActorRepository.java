package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.repository.customRepos.ActorRepositoryCustom;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long>, ActorRepositoryCustom {
    @Transactional
    @Query(value = """
    INSERT INTO actors (tmdb_id, name)
    VALUES (:tmdbId, :name)
    ON CONFLICT (tmdb_id)
    DO UPDATE SET name = EXCLUDED.name
    RETURNING id, tmdb_id, name
""", nativeQuery = true)
    Object upsertActor(@Param("tmdbId") Long tmdbId,
                       @Param("name") String name);
}
