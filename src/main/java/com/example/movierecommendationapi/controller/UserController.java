package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.UserDto;
import com.example.movierecommendationapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDto> getUser(@Parameter(description = "User ID") @PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/watchMovie/{movieId}")
    @Operation(summary = "Watch a movie")
    public ResponseEntity<com.example.movierecommendationapi.dto.WatchMovieResponse> watchMovie(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int movieMinutes
    ) {
        return ResponseEntity.ok(userService.watchMovie(movieId, movieMinutes));
    }

    @GetMapping("/watchMovie/{movieId}")
    @Operation(summary = "Check whether the current user has completed a movie")
    public ResponseEntity<com.example.movierecommendationapi.dto.WatchStatusDto> getWatchStatus(
            @PathVariable Long movieId
    ) {
        return ResponseEntity.ok(userService.getWatchStatus(movieId));
    }

    @GetMapping("/{id}/points/history")
    @Operation(summary = "Get a user's point-earning event history")
    public ResponseEntity<List<com.example.movierecommendationapi.dto.PointHistoryDto>> getPointHistory(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getPointHistory(id));
    }

    @GetMapping("/{id}/watches/count")
    @Operation(summary = "Number of movies this user has finished")
    public ResponseEntity<Integer> getWatchCount(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getCompletedWatchCount(id));
    }
}

