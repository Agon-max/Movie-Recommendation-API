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
    private final ActorMapper actorMapper;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper) {
        this.actorRepository = actorRepository;
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

    @Transactional
    // Create a new actor
    public ActorDto createActor(ActorDto actorDto) {
        Actor actor = new Actor();
        actor.setTmdbId(actorDto.getTmdbId());
        actor.setName(actorDto.getName());
        Actor savedActor = actorRepository.save(actor);
        return actorMapper.toDto(savedActor);
    }

    @Transactional
    // Update an actor
    public ActorDto updateActor(Long id, ActorDto actorDto) {
        Actor existingActor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Actor not found!"));
        actorMapper.updateManagedActor(actorDto, existingActor);
        return actorMapper.toDto(existingActor);
    }

    // Delete an actor
    public void deleteActor(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ResourceNotFound("Actor not found!");
        }
        actorRepository.deleteById(id);
    }

    public Actor getActorEntitiesByIds(Long id) {
        return actorRepository.findById(id).orElseThrow(
                () ->(new RuntimeException("Actor not found"))
        );
    }
}