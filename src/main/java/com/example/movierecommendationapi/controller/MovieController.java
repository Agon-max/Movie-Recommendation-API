package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.service.MovieService;
import com.example.movierecommendationapi.service.TmdbService;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie Controller", description = "Movie management endpoints")
public class MovieController{

    private final MovieService movieService;
    private final TmdbService tmdbService;

    public MovieController(MovieService movieService, TmdbService tmdbService) {
        this.movieService = movieService;
        this.tmdbService = tmdbService;
    }

   @PostMapping @ResponseStatus(HttpStatus.CREATED)
   @Operation(summary = "Create a new movie")
   public MovieDto saveMovie(MovieDto movieDto){
        return movieService.createMovie(movieDto);
   }

   @GetMapping("/search")
   @Operation(summary = "Get a movie by title")
   public ResponseEntity<Page<MovieDto>> getMovieByTitle(@RequestParam("title") String movieTitle, @PageableDefault(size = 10, sort = "title") Pageable pageable){
       return ResponseEntity.ok(movieService.getMovieByTitle(movieTitle, pageable));
   }

   @GetMapping("/{id}")
   @Operation(summary = "Get a movie by ID")
   public ResponseEntity<MovieDto> getMovieById(@PathVariable long id){
        if(!movieService.movieExists(id)) { return ResponseEntity.notFound().build(); }
        var movieDto = movieService.getMovieById(id);
        if(movieDto == null) { return ResponseEntity.notFound().build(); }
        return ResponseEntity.ok(movieDto);
   }

   @PutMapping("/{id}") @ResponseStatus(HttpStatus.OK)
   @Operation(summary = "Update a movie")
   public MovieDto updateMovie(@PathVariable Long id, MovieDto movieDto){
        return movieService.updateMovie(id, movieDto);
   }

   @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.OK)
   @Operation(summary = "Delete a movie")
   public boolean deleteMovie(@PathVariable long id){
        return movieService.deleteMovie(id);
   }

   @GetMapping("/popular")
   @Operation(summary = "Get popular movies")
   public ResponseEntity<TmdbMovieResponseDto> getPopularMovies(@RequestParam(defaultValue = "1") int page){
        return ResponseEntity.ok(tmdbService.getPopularMovies());
   }

}
