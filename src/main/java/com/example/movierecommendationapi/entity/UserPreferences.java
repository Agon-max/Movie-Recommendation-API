package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
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
    private String favoriteDirectors; // JSON: ["Director1"]

    @Column(columnDefinition = "TEXT")
    private String watchHistorySummary; // "Watched 10 movies: 5 Action, 3 Comedy, 2 Drama"

    @Column(columnDefinition = "TEXT")
    private String surveySummary; // "Likes Action and Sci-Fi, dislikes Horror"

    private Integer totalMoviesWatched;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void prePersist() {
        if (this.lastUpdated == null) {
            this.lastUpdated = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
