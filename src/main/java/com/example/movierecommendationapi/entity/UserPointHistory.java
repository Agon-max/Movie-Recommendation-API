package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_point_history")
@Getter
@Setter
public class UserPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PointEventType eventType;

    private int pointsReceived;

    private LocalDateTime createdAt;
}