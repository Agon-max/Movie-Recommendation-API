package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.MovieDto;
import com.example.movierecommendationapi.dto.*;
import com.example.movierecommendationapi.dto.TmdbActorDto;
import com.example.movierecommendationapi.dto.TmdbCreditsResponseDto;
import com.example.movierecommendationapi.dto.TmdbMovieDto;
import com.example.movierecommendationapi.entity.Actor;
import com.example.movierecommendationapi.entity.Director;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.entity.Movie;
import com.example.movierecommendationapi.mapper.ActorMapper;
import com.example.movierecommendationapi.mapper.DirectorMapper;
import com.example.movierecommendationapi.mapper.GenreMapper;
import com.example.movierecommendationapi.repository.ActorRepository;
import com.example.movierecommendationapi.repository.DirectorRepository;
import com.example.movierecommendationapi.repository.GenreRepository;
import com.example.movierecommendationapi.repository.MovieRepository;
import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TmdbService {

    private final RestTemplate restTemplate;
    private final MovieService movieService;
    private final ActorService actorService;
    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;
    private final DirectorService directorService;
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final DirectorMapper directorMapper;

    public TmdbService(RestTemplate restTemplate, MovieService movieService, ActorService actorService, DirectorService directorService, GenreRepository genreRepository, GenreMapper genreMapper, MovieRepository movieRepository, ActorRepository actorRepository, ActorMapper actorMapper,
                       DirectorRepository directorRepository, DirectorMapper directorMapper) {
        this.restTemplate = restTemplate;
        this.movieService = movieService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
        this.directorRepository = directorRepository;
        this.directorMapper = directorMapper;
    }

    @Value("${tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${tmdb.popular-movies-url}")
    private String tmdbPopularMoviesUrl;

    @Value("${tmdb.genre-url}")
    private String tmdbGenreUrl;

    @Value("${tmdb.credits-url}")
    private String tmdbCreditsUrl;

    //Get popular movies of the current year.
    public TmdbMovieResponseDto getPopularMovies() {
        return restTemplate.getForObject(tmdbPopularMoviesUrl + tmdbApiKey, TmdbMovieResponseDto.class);
    }

    // Get genres
    public TmdbGenreResponseDto getGenres() {
        return restTemplate.getForObject(tmdbGenreUrl, TmdbGenreResponseDto.class);
    }

    // Get credits for a movie
    public TmdbCreditsResponseDto getMovieCredits(Long movieId) {
        String url = tmdbCreditsUrl.replace("{movieId}", movieId.toString());
        return restTemplate.getForObject(url, TmdbCreditsResponseDto.class);
    }

    // Get popular movies with pagination
    public TmdbMovieResponseDto getPopularMovies(int page) {
        return restTemplate.getForObject(tmdbPopularMoviesUrl + tmdbApiKey + "&page=" + page, TmdbMovieResponseDto.class);
    }

    public void importExternalTMDBInfo(int movieCount) {

        Set<Long> processedMovieIds = new HashSet<>();
        int page = 1;
        int importedMovies = 0;

        while (importedMovies < movieCount && page <= 10) {

            TmdbMovieResponseDto response = getPopularMovies(page);

            for (TmdbMovieDto dto : response.getResults()) {

                if (!processedMovieIds.add(dto.getId())) continue;

                Movie movie = importMovie(dto);

                TmdbCreditsResponseDto credits = getMovieCredits(dto.getId());

                if (credits == null) continue;

                // ACTORS + DIRECTORS
                List<Actor> actors = importActorsFromCredits(credits);
                List<Director> directors = importDirectorsFromCredits(credits);

                // GENRES
                List<Genre> genres = resolveGenres(dto.getGenreIds());

                // LINK ALL RELATIONS
                movie.setActors(actors);
                movie.setDirectors(directors);
                movie.setGenres(genres);

                movieRepository.save(movie);

                importedMovies++;
                if (importedMovies >= movieCount) break;
            }

            page++;
        }
    }


    @Transactional
    protected Movie importMovie(TmdbMovieDto dto) {

        movieRepository.upsertMovie(
                dto.getId(),
                dto.getTitle(),
                dto.getOverview(),
                dto.getReleaseDate(),
                dto.getOriginalLanguage(),
                dto.getVoteAverage()
        );

        return movieRepository.findByTmdbId(dto.getId())
                .orElseThrow(() ->
                        new RuntimeException("Movie not found after upsert: " + dto.getId()));
    }



    private List<Actor> importActorsFromCredits(TmdbCreditsResponseDto credits) {

        List<Actor> actors = new ArrayList<>();

        if (credits.getCast() == null) {
            return actors;
        }

        Set<Long> seen = new HashSet<>();

        for (TmdbActorDto dto : credits.getCast()) {

            if (!seen.add(dto.getId())) {
                continue;
            }

            Object[] row = (Object[]) actorRepository.upsertActor(
                    dto.getId(),
                    dto.getName()
            );

            Actor actor = new Actor();

            actor.setId(((Number) row[0]).longValue());
            actor.setTmdbId(((Number) row[1]).longValue());
            actor.setName((String) row[2]);

            actors.add(actor);
        }

        return actors;
    }

    @Transactional
    protected List<Director> importDirectorsFromCredits(TmdbCreditsResponseDto credits) {

        List<Director> directors = new ArrayList<>();

        if (credits.getCrew() == null)
            return directors;

        Set<Long> processedDirectorIds = new HashSet<>();

        for (TmdbCrewMemberDto dto : credits.getCrew()) {

            if (!"Director".equals(dto.getJob()))
                continue;

            if (dto.getId() == null)
                continue;

            if (!processedDirectorIds.add(dto.getId())) {
                continue;
            }

            directorRepository.upsertDirector(
                    dto.getId(),
                    dto.getName()
            );

            Director director = directorRepository.findByTmdbId(dto.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Director not found after upsert: " + dto.getId()));

            directors.add(director);
        }

        return directors;
    }

    private List<Genre> resolveGenres(List<Long> genreIds) {

        List<Genre> genres = new ArrayList<>();

        if (genreIds == null) return genres;

        for (Long tmdbId : genreIds) {

            genreRepository.findByTmdbId(tmdbId)
                    .ifPresent(genres::add);
        }

        return genres;
    }

}


