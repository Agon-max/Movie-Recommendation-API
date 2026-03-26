package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MovieDto {
    @NotNull
    private Long id;
    @NotBlank
    @Size(max = 255)
    private String title;
    @Size(max = 2000)
    private String overview;

    @NotBlank
    @Size(max = 15)
    private String language;

    @NotEmpty
    @Size(max = 255)
    private List<@NotNull DirectorDto> director;

    @NotNull
    private LocalDateTime releaseDate;

    @NotEmpty
    private List<@NotBlank ActorDto> actors;

    @DecimalMin("0.0")
    @DecimalMax("10.0")

    private Double averageRating;

}
