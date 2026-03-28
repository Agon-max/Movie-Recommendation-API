package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.repository.GenreRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    public void importGenres() {}
}
