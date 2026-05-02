package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "genre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long tmdbId;

    private String title;
    @ManyToMany(mappedBy = "genres")
    private List<Movie> movies = new ArrayList<>();

    public Genre(Long id, Long tmdbId, String title) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
    }
}