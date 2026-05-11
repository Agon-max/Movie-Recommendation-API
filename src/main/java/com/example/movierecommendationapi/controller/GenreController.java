package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.GenreDto;
import com.example.movierecommendationapi.service.GenreService;
import com.example.movierecommendationapi.service.TmdbService;
import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@Tag(name = "Genre Controller", description = "Genre management endpoints")
public class GenreController {

    private final GenreService genreService;
    private final TmdbService tmdbService;

    public GenreController(GenreService genreService, TmdbService tmdbService) {
        this.genreService = genreService;
        this.tmdbService = tmdbService;
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if a genre with the given ID exists in the database.")
    public ResponseEntity<Boolean> genreExists(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.genreExists(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a genre by ID")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @GetMapping
    @Operation(summary = "Get all genres")
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @PostMapping
    @Operation(summary = "Create a new genre")
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        GenreDto created = genreService.createGenre(genreDto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a genre")
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable Long id,
            @RequestBody GenreDto genreDto) {

        GenreDto updated = genreService.updateGenre(id, genreDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre by ID")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    @Operation(summary = "Imports genres from the TMDB service into the local database.")
    public ResponseEntity<TmdbGenreResponseDto> importGenresFromExternalService(){
        return ResponseEntity.ok(genreService.importGenresIntoLocalDB());
    }
}