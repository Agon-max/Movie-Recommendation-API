package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.TMDBGenreDto;
import com.example.movierecommendationapi.dto.TMDBGenreResponseDto;
import com.example.movierecommendationapi.dto.TmdbResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TMDBService {

    @Value("${tmbd.api-key}")
    private String tmbdApiKey;

    @Value("${tmdb.movie-url}")
    private String tmdbUrl;

    @Value("${tmbd.genre-url}")
    private String tmdbGenreUrl;

    public TmdbResponseDto getPopularMovies() {

        String url = tmdbUrl + tmbdApiKey;

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, TmdbResponseDto.class);
    }

    public List<TMDBGenreDto> getGenres() {

        RestTemplate restTemplate = new RestTemplate();

        TMDBGenreResponseDto response =
                restTemplate.getForObject(tmdbGenreUrl, TMDBGenreResponseDto.class);

        return response.getGenres();
    }
}
