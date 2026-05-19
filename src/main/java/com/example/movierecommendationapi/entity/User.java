package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class   User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Email
    private String email;
    private String password; // Store securely (hashed)

    private int totalPoints;
    private boolean firstLogin = true;

    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Movie> favoriteMovies;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WatchHistory> watchHistory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PointTransaction> pointTransactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Redemption> redemptions;

    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL)
    private UserSurvey survey;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserPointHistory> user_point_history;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PointEvent> point_events;
}
