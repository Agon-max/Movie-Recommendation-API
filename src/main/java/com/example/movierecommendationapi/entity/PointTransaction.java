package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.entity.enums.PointTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Positive = credit (earned), negative = debit (redeemed/expired)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type;

    // The action that triggered this transaction (e.g. WATCH_MOVIE)
    @Enumerated(EnumType.STRING)
    private PointEventType sourceEventType;

    // ID of the source record (WatchHistory.id, Review.id, Redemption.id, etc.)
    private Long sourceId;

    private String description;

    private LocalDateTime createdAt;
}
