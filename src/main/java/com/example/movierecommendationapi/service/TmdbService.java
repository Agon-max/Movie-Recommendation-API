package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.*;
import com.example.movierecommendationapi.entity.*;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.*;
import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TmdbService {

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;
    private final ImportJobRepository importJobRepository;

    @Value("${tmdb.api-key}")
    private String tmdbApiKey;

    @Value("${tmdb.popular-movies-url}")
    private String tmdbPopularMoviesUrl;

    @Value("${tmdb.credits-url}")
    private String tmdbCreditsUrl;

    @Value("${tmdb.genre-url}")
    private String tmdbGenreUrl;

    @Value("${tmdb.movie-details}")
    private String tmdbMovieDetails;

    public TmdbService(
            RestTemplate restTemplate,
            MovieRepository movieRepository,
            ActorRepository actorRepository,
            DirectorRepository directorRepository,
            GenreRepository genreRepository,
            ImportJobRepository importJobRepository
    ) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.genreRepository = genreRepository;
        this.importJobRepository = importJobRepository;
    }

    // Get genres public
    TmdbGenreResponseDto getGenres() {
        return restTemplate.getForObject(tmdbGenreUrl, TmdbGenreResponseDto.class);
    }

    public TmdbMovieResponseDto getPopularMovies() {
        return restTemplate.getForObject(
                tmdbPopularMoviesUrl + tmdbApiKey,
                TmdbMovieResponseDto.class
        );
    }

    // -------------------------
    // PUBLIC ENTRY POINT
    // -------------------------
    public void importExternalTMDBInfo(
            int movieCount,
            ImportJob job
    ) {

        List<TmdbMovieDto> collectedMovies =
                fetchPopularMovies(movieCount);

        List<Movie> savedMovies =
                saveBasicMovies(collectedMovies);

        enrichMovies(savedMovies, job);
    }

    // -------------------------
    // STEP 1: FETCH MOVIES ONLY
    // -------------------------
    private List<TmdbMovieDto> fetchPopularMovies(int movieCount) {

        List<TmdbMovieDto> results = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        int page = 1;

        while (results.size() < movieCount && page <= 10) {

            TmdbMovieResponseDto response =
                    restTemplate.getForObject(
                            tmdbPopularMoviesUrl + tmdbApiKey + "&page=" + page,
                            TmdbMovieResponseDto.class
                    );

            if (response == null || response.getResults() == null) break;

            for (TmdbMovieDto dto : response.getResults()) {

                if (dto.getId() == null) continue;
                if (!seen.add(dto.getId())) continue;

                results.add(dto);

                if (results.size() >= movieCount) break;
            }

            page++;
        }

        return results;
    }

    private int getMovieRuntime(Long movieId) {

        String url = tmdbMovieDetails + movieId + "?api_key=" + tmdbApiKey;

        TmdbMovieDetailsDto movie = restTemplate.getForObject(
                url,
                TmdbMovieDetailsDto.class
        );

        if (movie == null) {
            throw new ResourceNotFound("Movie details not found");
        }

        return movie.getRuntime();
    }
    // -------------------------
    // STEP 2: SAVE MOVIES ONLY
    // -------------------------
    @Transactional
    protected List<Movie> saveBasicMovies(List<TmdbMovieDto> dtos) {

        List<Movie> movies = new ArrayList<>();

        for (TmdbMovieDto dto : dtos) {

            Movie movie = movieRepository.findByTmdbId(dto.getId())
                    .orElseGet(Movie::new);

            movie.setTmdbId(dto.getId());
            movie.setTitle(dto.getTitle());
            movie.setOverview(dto.getOverview());
            movie.setReleaseDate(dto.getReleaseDate());
            movie.setLanguage(dto.getOriginalLanguage());
            movie.setAverageRating(dto.getVoteAverage());
            movie.setRuntimeMinutes(getMovieRuntime(dto.getId()));

            movies.add(movie);
        }

        return movieRepository.saveAll(movies);
    }

    private void enrichMovies(
            List<Movie> movies,
            ImportJob job
    ) {

        for (Movie movie : movies) {

            try {

                TmdbCreditsResponseDto credits =
                        fetchCredits(movie.getTmdbId());

                if (credits == null)
                    continue;

                movie.setActors(importActors(credits));
                movie.setDirectors(importDirectors(credits));
                movie.setGenres(resolveGenres());

                movieRepository.save(movie);

            } catch (Exception e) {

                System.out.println(
                        "Failed movie: " + movie.getTmdbId()
                );
            }

            // -------------------------
            // UPDATE PROGRESS
            // -------------------------
            job.setProcessedMovies(
                    job.getProcessedMovies() + 1
            );

            importJobRepository.save(job);
        }
    }

    // -------------------------
    // CREDITS API
    // -------------------------
    private TmdbCreditsResponseDto fetchCredits(Long movieId) {

        String url = tmdbCreditsUrl.replace("{movieId}", movieId.toString());

        return restTemplate.getForObject(url, TmdbCreditsResponseDto.class);
    }

    // -------------------------
    // ACTORS
    // -------------------------
    private List<Actor> importActors(TmdbCreditsResponseDto credits) {

        List<Actor> actors = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        if (credits.getCast() == null) return actors;

        for (TmdbActorDto dto : credits.getCast()) {

            if (dto.getId() == null) continue;
            if (!seen.add(dto.getId())) continue;

            actorRepository.upsertActor(dto.getId(), dto.getName());

            actorRepository.findByTmdbId(dto.getId())
                    .ifPresent(actors::add);
        }

        return actors;
    }

    // -------------------------
    // DIRECTORS
    // -------------------------
    private List<Director> importDirectors(TmdbCreditsResponseDto credits) {

        List<Director> directors = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        if (credits.getCrew() == null) return directors;

        for (TmdbCrewMemberDto dto : credits.getCrew()) {

            if (!"Director".equals(dto.getJob())) continue;
            if (dto.getId() == null) continue;
            if (!seen.add(dto.getId())) continue;

            directorRepository.upsertDirector(dto.getId(), dto.getName());

            directorRepository.findByTmdbId(dto.getId())
                    .ifPresent(directors::add);
        }

        return directors;
    }

    // -------------------------
    // GENRES
    // -------------------------
    private List<Genre> resolveGenres() {

        List<Genre> genres = new ArrayList<>();

        List<TmdbGenreDto> tmdbGenres = getGenres().getGenres();

        for (TmdbGenreDto tmdbGenre : tmdbGenres) {

            var genreExists = genreRepository.findByTmdbId(tmdbGenre.getId());

            if(genreExists.isPresent())
                continue;

            Genre genre = new Genre();

            genre.setTmdbId(tmdbGenre.getId());
            genre.setTitle(tmdbGenre.getName());

            genres.add(genre);
        }
        return genres;
    }
}