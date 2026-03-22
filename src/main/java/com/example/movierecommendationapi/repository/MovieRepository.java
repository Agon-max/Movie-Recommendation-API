package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository <Movie, Long>{
}
