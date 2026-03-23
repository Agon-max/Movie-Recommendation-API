package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController{

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
}
