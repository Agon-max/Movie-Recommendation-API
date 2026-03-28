package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.repository.ActorRepository;
import org.springframework.stereotype.Service;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public void importActors() {}
}
