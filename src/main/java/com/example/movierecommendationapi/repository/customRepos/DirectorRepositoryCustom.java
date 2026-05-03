package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Director;

import java.util.List;

public interface DirectorRepositoryCustom {
    List<Director> getDirectorsByMovie(Long movieId, String movieTitle);
}
