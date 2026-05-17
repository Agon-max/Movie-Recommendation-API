package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.DirectorDto;
import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.DirectorMapper;
import com.example.movierecommendationapi.repository.DirectorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;
    private final MovieService movieService;

    public DirectorService(DirectorRepository directorRepository, DirectorMapper directorMapper, MovieService movieService) {
        this.directorRepository = directorRepository;
        this.directorMapper = directorMapper;
        this.movieService = movieService;
    }

    // Check if director exists
    public boolean directorExists(Long id) {
        return directorRepository.existsById(id);
    }


    // Get directors by movie
    public List<DirectorDto> getAllDirectorsByMovie(Long movieId, String movieTitle) {
        if (movieId != null && !movieService.movieExists(movieId)) {
            throw new ResourceNotFound("Movie not found!");
        }

        if ((movieId == null) && (movieTitle == null || movieTitle.isBlank())) {
            // If no filters, get all directors (fallback to original behavior)
            List<Director> directors = directorRepository.findAll();
            return directors.stream()
                    .map(directorMapper::toDto)
                    .collect(Collectors.toList());
        }

        var directors = directorRepository.getDirectorsByMovie(movieId, movieTitle);

        return directors.stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get director by ID
    public DirectorDto getDirectorById(Long id) {
        return directorRepository.findById(id)
                .map(directorMapper::toDto)
                .orElseThrow(() -> new ResourceNotFound("Director not found!"));
    }

    // Create a new director
    public DirectorDto createDirector(DirectorDto directorDto) {
        Director director = new Director();
        director.setTmdbId(directorDto.getTmdbId());
        director.setName(directorDto.getName());
        Director savedDirector = directorRepository.save(director);
        return directorMapper.toDto(savedDirector);
    }

    @Transactional
    // Update an existing director
    public DirectorDto updateDirector(Long id, DirectorDto directorDto) {
        Director existingDirector = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Director not found!"));
        directorMapper.updateManagedDirector(directorDto, existingDirector);
        return directorMapper.toDto(existingDirector);
    }

    // Delete a director
    public void deleteDirector(Long id) {
        if (!directorRepository.existsById(id)) {
            throw new ResourceNotFound("Director not found!");
        }
        directorRepository.deleteById(id);
    }

    public Director getDirectorEntityById(Long id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Director not found"));
    }
}
