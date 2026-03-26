package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.TmdbResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TMDBService {

    @Value("${tmbd.api-key}")
    private String tmbdApiKey;

    public TmdbResponseDto getPopularMovies() {

        String url =
                "https://api.themoviedb.org/3/movie/popular?api_key=" + tmbdApiKey;

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, TmdbResponseDto.class);
    }

}
