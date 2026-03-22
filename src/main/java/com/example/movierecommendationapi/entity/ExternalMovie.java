package com.example.movierecommendationapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "external_movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tmbdId;  // External API ID
    private String title;
    private String posterUrl;
    private String language;
    private Integer runtime;
    private String overview;

    @OneToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

}
