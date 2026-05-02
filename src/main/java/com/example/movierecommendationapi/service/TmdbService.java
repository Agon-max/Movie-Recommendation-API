package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TmdbService {

    private final RestTemplate restTemplate;

    public TmdbService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${tmbd.api-key}")
    private String tmbdApiKey;

    @Value("${tmdb.popular-movies-url}")
    private String tmdbPopularMoviesUrl;

    @Value("${tmbd.genre-url}")
    private String tmdbGenreUrl;

    //Get popular movies of the current year.
    public TmdbMovieResponseDto getPopularMovies() {
        return restTemplate.getForObject(tmdbPopularMoviesUrl, TmdbMovieResponseDto.class);
    }

    public TmdbGenreResponseDto getGenres() {
        return restTemplate.getForObject(tmdbGenreUrl, TmdbGenreResponseDto.class);
    }




}


