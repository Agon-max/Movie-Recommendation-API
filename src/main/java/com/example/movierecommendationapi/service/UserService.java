package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.UserDto;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.WatchHistory;
import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.UserMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.repository.UserRepository;
import com.example.movierecommendationapi.repository.WatchHistoryRepository;
import com.example.movierecommendationapi.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MovieRepository movieRepository;
    private final WatchHistoryRepository historyRepository;
    private final PointService pointService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, MovieRepository movieRepository, WatchHistoryRepository watchHistoryRepository, PointService pointService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.movieRepository = movieRepository;
        historyRepository = watchHistoryRepository;
        this.pointService = pointService;
    }

    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username '" + userDto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email '" + userDto.getEmail() + "' is already in use");
        }

        User user = userMapper.toEntity(userDto);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));  // Encode the password
        user.setTotalPoints(userDto.getTotalPoints() != 0 ? userDto.getTotalPoints() : 0);  // Default to 0 if not set
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDto.getUsername());
                    user.setEmail(userDto.getEmail());
                    user.setTotalPoints(userDto.getTotalPoints());
                    User updatedUser = userRepository.save(user);
                    return userMapper.toDto(updatedUser);
                })
                .orElse(null);
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void watchMovie(Long movieId, int watchedMinutes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFound("There is no active user!");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) auth.getPrincipal();

        Long userId = userDetails.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFound("Movie not found"));

        WatchHistory history = historyRepository
                .findByUserAndMovie(user, movie)
                .orElse(new WatchHistory());

        history.setUser(user);
        history.setMovie(movie);

        int runtime = movie.getRuntimeMinutes();

        if (runtime <= 0) {
            throw new IllegalStateException("Movie runtime missing");
        }

        double percentage = (watchedMinutes * 100.0) / runtime;

        history.setWatchedMinutes(watchedMinutes);

        if (percentage >= 90 && !history.isCompleted()) {

            history.setCompleted(true);

            pointService.awardPoints(
                    user,
                    PointEventType.WATCH_MOVIE
            );
        }

        historyRepository.save(history);
    }

    public User getUserByEntityId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }
}
