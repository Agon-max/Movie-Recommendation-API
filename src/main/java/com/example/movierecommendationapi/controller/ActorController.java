package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.service.ActorService;
import com.example.movierecommendationapi.service.TmdbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@Tag(name = "Actor Controller", description = "Actor management endpoints")
public class ActorController {

    private final ActorService actorService;
    private final TmdbService tmdbService;

    public ActorController(ActorService actorService, TmdbService tmdbService) {
        this.actorService = actorService;
        this.tmdbService = tmdbService;
    }

    // Get actor by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get an actor by ID")
    public ResponseEntity<ActorDto> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }



    // Create a new actor
    @PostMapping
    @Operation(summary = "Create a new actor")
    public ResponseEntity<ActorDto> createActor(ActorDto actorDto) {
        ActorDto createdActor = actorService.createActor(actorDto);
        return ResponseEntity.ok(createdActor);
    }

    // Update actor
    @PutMapping("/{id}")
    @Operation(summary = "Update an actor")
    public ResponseEntity<ActorDto> updateActor(
            @PathVariable Long id,
            @RequestBody ActorDto actorDto) {

        ActorDto updatedActor = actorService.updateActor(id, actorDto);
        return ResponseEntity.ok(updatedActor);
    }

    // Delete actor
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an actor")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}
