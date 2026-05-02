package com.example.movierecommendationapi.repository.repoimpl;

import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.repository.customRepos.MovieRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class MovieRepositoryImpl implements MovieRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    public MovieRepositoryImpl() {

    }

//    @Override
//    public Movie updateMovie(Movie movie) {
//        return entityManager.merge(movie);
//    }
}
