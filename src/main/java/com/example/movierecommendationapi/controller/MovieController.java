package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.service.MovieService;
import com.example.movierecommendationapi.service.TMDBService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController{

    private final MovieService movieService;
    private final TMDBService tmdbService;

    public MovieController(MovieService movieService, TMDBService tmdbService) {
        this.movieService = movieService;
        this.tmdbService = tmdbService;
    }

   @PostMapping @ResponseStatus(HttpStatus.CREATED)
   @Operation(summary = "Create a new movie")
    public MovieDto saveMovie(MovieDto movieDto){
       return null;
   }

    @PostMapping("/import")
    public void importMovies() {
        movieService.importMovies();
    }

}
