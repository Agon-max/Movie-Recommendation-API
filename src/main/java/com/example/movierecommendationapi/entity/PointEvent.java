package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointEventType eventType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // How many points this event awards
    private int pointsAwarded;

    @Column(length = 500)
    private String description;

    private LocalDateTime createdAt;

    // Allows disabling an event without deleting it
    private boolean active = true;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
