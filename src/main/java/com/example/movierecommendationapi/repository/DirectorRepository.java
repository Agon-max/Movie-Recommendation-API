package com.example.movierecommendationapi.repository;

import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.repository.customRepos.DirectorRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long>, DirectorRepositoryCustom {
}
