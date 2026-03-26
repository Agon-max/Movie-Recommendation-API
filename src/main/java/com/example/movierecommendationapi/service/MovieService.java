package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.TmdbMovieDto;
import com.example.movierecommendationapi.dto.TmdbResponseDto;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TMDBService tmdbService;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, TMDBService tmdbService) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.tmdbService = tmdbService;
    }

    public MovieDto saveMovie(MovieDto movieDto) {
        var savedMovie = movieRepository.save(movieMapper.toEntity(movieDto));
        return movieMapper.toDto(savedMovie);
    }

    public void importMovies() {

        TmdbResponseDto response = tmdbService.getPopularMovies();

        for (TmdbMovieDto dto : response.getResults()) {

            Movie movie = new Movie();
            Genre genre = new Genre();

            movie.setTitle(dto.getTitle());

            movie.setOverview(dto.getOverview());

            movieRepository.save(movie);
        }
    }
}
