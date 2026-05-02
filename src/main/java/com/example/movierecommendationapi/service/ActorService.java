package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.ActorMapper;
import com.example.movierecommendationapi.repository.ActorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final TmdbService tmdbService;
    private final ActorMapper actorMapper;

    public ActorService(ActorRepository actorRepository, TmdbService tmdbService, ActorMapper actorMapper) {
        this.actorRepository = actorRepository;
        this.tmdbService = tmdbService;
        this.actorMapper = actorMapper;
    }

    // Check if actor exists
    public boolean actorExists(Long id) {
        return actorRepository.existsById(id);
    }

    // Get actor by Id
    public ActorDto getActorById(Long id) {
        return actorRepository.findById(id)
                .map(actorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFound("Actor not found!"));
    }

    // Get all actors
    public List<ActorDto> getAllActors() {
        return actorRepository.findAll()
                .stream()
                .map(actorMapper::toDto)
                .collect(Collectors.toList());
    }

    // Create a new actor
    public ActorDto createActor(ActorDto actorDto) {
        Actor actor = actorMapper.toEntity(actorDto);
        Actor savedActor = actorRepository.save(actor);
        return actorMapper.toDto(savedActor);
    }

    @Transactional
    // Update an actor
    public ActorDto updateActor(Long id, ActorDto actorDto) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Actor not found!"));

        // Option 1: manual update (recommended for control)
        existingActor.setName(actorDto.getName());
        // add other fields here

        Actor updatedActor = actorRepository.save(existingActor);
        return actorMapper.toDto(updatedActor);
    }

    // Delete an actor
    public void deleteActor(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ResourceNotFound("Actor not found!");
        }
        actorRepository.deleteById(id);
    }
}