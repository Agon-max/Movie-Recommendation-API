package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String director;
    private LocalDateTime releaseDate;
    private List<String> actors;

    private Double averageRating;

    @ManyToMany(mappedBy = "favoriteMovies")
    private List<User> favoritedBy;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<WatchHistory> watchHistory;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Review> reviews;

}
