package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(unique = true, nullable = false)
    private PointEventType eventType;

    // How many points this event awards
    private int pointsAwarded;

    private String description;

    // Allows disabling an event without deleting it
    private boolean active;
}
