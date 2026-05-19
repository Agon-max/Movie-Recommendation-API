package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.repository.UserRepository;
import com.example.movierecommendationapi.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }
        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), null);
    }
}