package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    public MovieDto saveMovie(MovieDto movieDto) {
        var savedMovie = movieRepository.save(movieMapper.toEntity(movieDto));
        return movieMapper.toDto(savedMovie);
    }


}
