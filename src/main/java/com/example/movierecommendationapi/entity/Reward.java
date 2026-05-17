package com.example.movierecommendationapi.entity;

import com.example.movierecommendationapi.entity.enums.RewardType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    // Points a user must spend to claim this reward
    private int pointCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType type;

    @OneToMany
    private List<UserRedemptionHistory> userRedemptionHistory = new ArrayList<UserRedemptionHistory>();

    private int stock;

    // Dollar/monetary value of the reward (e.g. $10.00 gift card)
    private BigDecimal monetaryValue;

    // Allows hiding a reward without deleting it
    private boolean active;

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Redemption> redemptions;
}
