package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.LoginRequest;
import com.example.movierecommendationapi.dto.LoginResponse;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.repository.UserRepository;
import com.example.movierecommendationapi.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return null;
        }

        String token = tokenProvider.generateToken(user.getUsername());
        return new LoginResponse(token, user.getUsername(), user.getId());
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setTotalPoints(0);

        return userRepository.save(user);
    }
}
