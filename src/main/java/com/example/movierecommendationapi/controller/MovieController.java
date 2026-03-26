package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController{

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/")
    @Operation(summary = "First Spring Boot API testing")
    public String home() {
        return "API running";
    }

   @PostMapping
   @Operation(summary = "Create a new movie")
    public MovieDto saveMovie(MovieDto movieDto){
       return null;
   }

    @PostMapping("/import")
    public void importMovies() {
        movieService.importMovies();
    }
}
