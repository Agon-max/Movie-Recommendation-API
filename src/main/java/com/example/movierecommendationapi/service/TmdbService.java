package com.example.movierecommendationapi.service;

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


        String url = tmdbUrl + tmbdApiKey;

        RestTemplate restTemplate = new RestTemplate();

    }


