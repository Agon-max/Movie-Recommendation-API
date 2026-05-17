package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.ImportJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalMovies;

    private int processedMovies;

    @Enumerated(EnumType.STRING)
    private ImportJobStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String errorMessage;
}