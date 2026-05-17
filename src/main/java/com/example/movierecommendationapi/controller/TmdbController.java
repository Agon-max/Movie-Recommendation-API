package com.example.movierecommendationapi.controller;


import com.example.movierecommendationapi.entity.ImportJob;
import com.example.movierecommendationapi.entity.enums.ImportJobStatus;
import com.example.movierecommendationapi.repository.ImportJobRepository;
import com.example.movierecommendationapi.service.TmdbImportService;
import com.example.movierecommendationapi.service.TmdbService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/external/movies")
@Tag(name = "Tmdb Controller", description = "Tmdb management endpoints")
public class TmdbController {

    private final TmdbImportService tmdbImportService;

    public TmdbController(TmdbImportService tmdbImportService) {
        this.tmdbImportService = tmdbImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<Long> startImport(@RequestParam int count) {
        return ResponseEntity.ok(tmdbImportService.startImport(count));
    }

    @GetMapping("/import/{jobId}")
    public ResponseEntity<ImportJob> getJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(tmdbImportService.getJobById(jobId));
    }
}
