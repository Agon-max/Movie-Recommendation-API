package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.GenreDto;
import com.example.movierecommendationapi.service.GenreService;
import com.example.movierecommendationapi.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;
    private final TmdbService tmdbService;

    public GenreController(GenreService genreService, TmdbService tmdbService) {
        this.genreService = genreService;
        this.tmdbService = tmdbService;
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> genreExists(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.genreExists(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        GenreDto created = genreService.createGenre(genreDto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(
            @PathVariable Long id,
            @RequestBody GenreDto genreDto) {

        GenreDto updated = genreService.updateGenre(id, genreDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}