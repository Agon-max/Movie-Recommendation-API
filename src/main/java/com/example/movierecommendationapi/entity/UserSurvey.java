package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_surveys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String favoriteGenres; // JSON: ["Action", "Comedy"]

    @Column(columnDefinition = "TEXT")
    private String favoriteActors; // JSON: ["Actor1", "Actor2"]

    @Column(columnDefinition = "TEXT")
    private String favoriteDirectors; // JSON: ["Director1", "Director2"]

    private String dislikes; // Optional: genres/content user dislikes

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
