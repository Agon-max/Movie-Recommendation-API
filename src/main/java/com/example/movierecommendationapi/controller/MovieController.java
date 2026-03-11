package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController{

    @GetMapping
    public List<MovieDto> searchMovies(){

        var movie = new MovieDto();
        List<MovieDto> movies = new ArrayList<MovieDto>();

        movies.add(movie);
        return movies;
    }
}
