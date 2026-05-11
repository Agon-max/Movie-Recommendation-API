package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.DirectorDto;
import com.example.movierecommendationapi.service.DirectorService;
import com.example.movierecommendationapi.service.MovieService;
import com.example.movierecommendationapi.service.TmdbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;

@RestController
@RequestMapping("/api/directors")
@Tag(name = "Director Controller", description = "Director management endpoints")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @Operation(summary = "Get all directors based on movie Id and Title")
    public ResponseEntity<List<DirectorDto>> getAllDirectorsByMovie(Long movieId, String movieTitle) {
        if(movieId == null && movieTitle == null){
            throw new ResolutionException("Movie ID or Title must be provided");
        }
        return ResponseEntity.ok(directorService.getAllDirectorsByMovie(movieId, movieTitle));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a director by ID")
    public ResponseEntity<DirectorDto> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new director")
    public ResponseEntity<DirectorDto> createDirector(@RequestBody DirectorDto directorDto) {
        return ResponseEntity.ok(directorService.createDirector(directorDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing director")
    public ResponseEntity<DirectorDto> updateDirector(@PathVariable Long id, @RequestBody DirectorDto directorDto) {
        return ResponseEntity.ok(directorService.updateDirector(id, directorDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a director by ID")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }

}
