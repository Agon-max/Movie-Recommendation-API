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
    private final MovieService movieService;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper, MovieService movieService) {
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
        this.movieService = movieService;
    }

    // Check if actor exists
    public boolean actorExists(Long id) {
        return actorRepository.existsById(id);
    }

    public List<ActorDto> getAllActorsByMovie(Long movieId, String movieTitle) {

        if (movieId != null && !movieService.movieExists(movieId)) {
            throw new ResourceNotFound("Movie not found!");
        }

        if ((movieId == null) && (movieTitle == null || movieTitle.isBlank())) {
            throw new IllegalArgumentException("At least one filter must be provided");
        }

        var actors = actorRepository.getActorsByMovie(movieId, movieTitle);

        return actors.stream()
                .map(actorMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get actor by Id
    public ActorDto getActorById(Long id) {
        return actorRepository.findById(id)
                .map(actorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFound("Actor not found!"));
    }
    // Get all actors

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
}