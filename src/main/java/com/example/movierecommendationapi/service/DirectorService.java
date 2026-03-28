package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.repository.DirectorRepository;
import org.springframework.stereotype.Service;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public void importDirectors() {}
}
