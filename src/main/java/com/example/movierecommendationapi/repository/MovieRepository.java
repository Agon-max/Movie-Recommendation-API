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

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository <Movie, Long>, MovieRepositoryCustom {

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title ,'%'))")
    public Page<Movie> getMovieByTitle(@Param("title") String title, Pageable pageable);
    List<Movie> findTop500ByOrderByAverageRatingDesc();
}
