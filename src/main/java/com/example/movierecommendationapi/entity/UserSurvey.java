package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.converter.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> favoriteGenres;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> favoriteActors;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> favoriteDirectors;

    private String dislikes;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}
