package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.ActorDto;
import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.TmdbMovieDto;
import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    // Method to create a new movie resource/record
    public MovieDto createMovie(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setTmdbId(movieDto.getTmdbId());
        movie.setTitle(movieDto.getTitle());
        movie.setLanguage(movieDto.getLanguage());
        movie.setOverview(movieDto.getOverview());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setAverageRating(movieDto.getAverageRating());
        movieRepository.save(movie);
        return movieMapper.toDto(movie);
    }

    // Get movie by title using pagination for performance
    @Transactional
    public Page<MovieDto> getMovieByTitle(String movieTitle, Pageable pageable){
        var movieToReturn = movieRepository.getMovieByTitle(movieTitle, pageable);
        return movieToReturn.map(movieMapper::toDto);
    }

    // Convenient method to check if a movie exists
    public boolean movieExists(Long id) {
        return movieRepository.existsById(id);
    }

    // Get movie by ID
    public MovieDto getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDto)
                .orElseThrow(()-> new ResourceNotFound("Movie not found!"));
    }

    // Update movie
    @Transactional
    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Movie not found"));

        movieMapper.updateMovieFromDto(movieDto, movie);
        return movieMapper.toDto(movie);
    }

    // Delete movie
    public boolean deleteMovie(long id){
        if(!movieExists(id) || getMovieById(id) == null){
            throw new ResourceNotFound("Movie not found!");
        }
        var movie = getMovieById(id);
        movieRepository.delete(movieMapper.toEntity(movie));
        return true;
    }

}
