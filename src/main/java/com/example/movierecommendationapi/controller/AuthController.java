package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.LoginRequest;
import com.example.movierecommendationapi.dto.LoginResponse;
import com.example.movierecommendationapi.dto.RegisterRequest;
import com.example.movierecommendationapi.dto.UserDto;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.mapper.UserMapper;
import com.example.movierecommendationapi.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user with username, email, and password")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest registerRequest) {
        User registeredUser = authService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(registeredUser));
    }
}
