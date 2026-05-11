package com.example.movierecommendationapi.controller;


import com.example.movierecommendationapi.service.TmdbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external/movies")
@Tag(name = "Tmdb Controller", description = "Tmdb management endpoints")
public class TmdbController {

    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/import")
    public ResponseEntity<Void> importExternalInfoIntLocalDB(int movieCount){
        tmdbService.importExternalTMDBInfo(movieCount);
        return ResponseEntity.ok().build();
    }
}
