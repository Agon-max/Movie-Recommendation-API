package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.TmdbGenreDto;
import com.example.movierecommendationapi.dto.TmdbMovieDto;
import com.example.movierecommendationapi.dto.TmdbMovieResponseDto;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.mapper.MovieMapper;
import com.example.movierecommendationapi.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TmdbService tmdbService;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, TmdbService tmdbService) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.tmdbService = tmdbService;
    }

    public MovieDto saveMovie(MovieDto movieDto) {
        var savedMovie = movieRepository.save(movieMapper.toEntity(movieDto));
        return movieMapper.toDto(savedMovie);
    }

    public boolean movieExists(Long id) {
        return movieRepository.existsById(id);
    }

    public void importMovies() {

        TmdbMovieResponseDto response = tmdbService.getPopularMovies();

        Map<Long, Genre> genreMap = tmdbService.getGenres()
                .stream()
                .collect(Collectors.toMap(TmdbGenreDto::getId,
                        g -> new Genre(g.getId(), g.getTitle())));

        for (TmdbMovieDto dto : response.getResults()) {

            if (movieRepository.existsById(dto.getId()))
                continue;

            Movie movie = new Movie();

            List<Genre> genres = dto.getGenre_ids()
                    .stream()
                    .map(genreMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            movie.setTitle(dto.getTitle());
            movie.setOverview(dto.getOverview());
            movie.setGenres(genres);

            movieRepository.save(movie);
        }
    }


}
