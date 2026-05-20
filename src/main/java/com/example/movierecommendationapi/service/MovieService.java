package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.ActorRepository;
import com.example.movierecommendationapi.repository.DirectorRepository;
import com.example.movierecommendationapi.repository.GenreRepository;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, GenreRepository genreRepository, DirectorRepository directorRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
    }

    @Transactional
    public MovieDto createMovie(MovieDto movieDto) {

        Movie movie = new Movie();

        movie.setTmdbId(movieDto.getTmdbId());
        movie.setTitle(movieDto.getTitle());
        movie.setLanguage(movieDto.getLanguage());
        movie.setOverview(movieDto.getOverview());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setAverageRating(movieDto.getAverageRating());
        movie.setPosterPath(movieDto.getPosterPath());
        movie.setBackdropPath(movieDto.getBackdropPath());

        movie.setGenres(
                movieDto.getGenreIds() == null || movieDto.getGenreIds().isEmpty()
                        ? new ArrayList<>()
                        : movieDto.getGenreIds()
                        .stream()
                        .map(id -> genreRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Genre not found: " + id)))
                        .toList()
        );

        movie.setActors(
                movieDto.getActorIds() == null || movieDto.getActorIds().isEmpty()
                        ? new ArrayList<>()
                        : movieDto.getActorIds()
                        .stream()
                        .map(id -> actorRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Actor not found: " + id)))
                        .toList()
        );

        movie.setDirectors(
                movieDto.getDirectorIds() == null || movieDto.getDirectorIds().isEmpty()
                        ? new ArrayList<>()
                        : movieDto.getDirectorIds()
                        .stream()
                        .map(id -> directorRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Director not found: " + id)))
                        .toList()
        );

        Movie savedMovie = movieRepository.save(movie);

        MovieDto response = new MovieDto();

        response.setId(savedMovie.getId());
        response.setTmdbId(savedMovie.getTmdbId());
        response.setTitle(savedMovie.getTitle());
        response.setLanguage(savedMovie.getLanguage());
        response.setOverview(savedMovie.getOverview());
        response.setReleaseDate(savedMovie.getReleaseDate());
        response.setAverageRating(savedMovie.getAverageRating());
        response.setPosterPath(savedMovie.getPosterPath());
        response.setBackdropPath(savedMovie.getBackdropPath());

        response.setActorIds(
                savedMovie.getActors()
                        .stream()
                        .map(Actor::getId)
                        .toList()
        );

        response.setDirectorIds(
                savedMovie.getDirectors()
                        .stream()
                        .map(Director::getId)
                        .toList()
        );

        response.setGenreIds(
                savedMovie.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .toList()
        );

        return response;
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

    public MovieDto getMovieById(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Movie not found!"));

        MovieDto dto = new MovieDto();

        dto.setTmdbId(movie.getTmdbId());
        dto.setTitle(movie.getTitle());
        dto.setOverview(movie.getOverview());
        dto.setLanguage(movie.getLanguage());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setAverageRating(movie.getAverageRating());
        dto.setPosterPath(movie.getPosterPath());
        dto.setBackdropPath(movie.getBackdropPath());

        dto.setActorIds(
                movie.getActors()
                        .stream()
                        .map(Actor::getId)
                        .toList()
        );

        dto.setDirectorIds(
                movie.getDirectors()
                        .stream()
                        .map(Director::getId)
                        .toList()
        );

        dto.setGenreIds(
                movie.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .toList()
        );

        return dto;
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
    public Movie getMovieEntityById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Movie not found!"));
    }

}
