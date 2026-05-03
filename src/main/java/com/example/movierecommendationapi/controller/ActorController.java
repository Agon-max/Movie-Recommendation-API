package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.service.ActorService;
import com.example.movierecommendationapi.service.TmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;
    private final TmdbService tmdbService;

    public ActorController(ActorService actorService, TmdbService tmdbService) {
        this.actorService = actorService;
        this.tmdbService = tmdbService;
    }

    // Get actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    // Get all actors
    @GetMapping
    public ResponseEntity<List<ActorDto>> getAllActorsByMovie(Long movieId, String movieTitle) {
        return ResponseEntity.ok(actorService.getAllActorsByMovie(movieId, movieTitle));
    }

    // Create actor
    @PostMapping
    public ResponseEntity<ActorDto> createActor(@RequestBody ActorDto actorDto) {
        ActorDto createdActor = actorService.createActor(actorDto);
        return ResponseEntity.ok(createdActor);
    }

    // Update actor
    @PutMapping("/{id}")
    public ResponseEntity<ActorDto> updateActor(
            @PathVariable Long id,
            @RequestBody ActorDto actorDto) {

        ActorDto updatedActor = actorService.updateActor(id, actorDto);
        return ResponseEntity.ok(updatedActor);
    }

    // Delete actor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}
