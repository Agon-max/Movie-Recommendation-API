package com.example.movierecommendationapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Accept in input, exclude from output
    private String password;
}
