package com.example.movierecommendationapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotNull
    private Long id;
    
    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotNull
    private int totalPoints;
}
