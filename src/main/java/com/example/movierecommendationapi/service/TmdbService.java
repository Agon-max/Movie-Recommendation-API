package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.TmdbGenreDto;
import com.example.movierecommendationapi.dto.TmdbGenreResponseDto;
import com.example.movierecommendationapi.dto.TmdbMovieResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TmdbService {

    @Value("${tmbd.api-key}")
    private String tmbdApiKey;

    @Value("${tmdb.movie-url}")
    private String tmdbUrl;

    @Value("${tmbd.genre-url}")
    private String tmdbGenreUrl;

    public TmdbMovieResponseDto getPopularMovies() {

        String url = tmdbUrl + tmbdApiKey;

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, TmdbMovieResponseDto.class);
    }

    public List<TmdbGenreDto> getGenres() {

        RestTemplate restTemplate = new RestTemplate();

        TmdbGenreResponseDto response =
                restTemplate.getForObject(tmdbGenreUrl, TmdbGenreResponseDto.class);

        return response.getGenres();
    }
}
