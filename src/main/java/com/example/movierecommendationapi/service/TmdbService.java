package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.*;
import com.example.movierecommendationapi.entity.*;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.*;
import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import com.example.movierecommendationapi.wrapper.TmdbMovieResponseDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // Dedicated Jackson 2.x mapper — Spring Boot 4 uses Jackson 3.x by default
    // (tools.jackson.*), which doesn't recognise our @JsonProperty annotations
    // from com.fasterxml.jackson.annotation. We bypass RestTemplate's converter
    // chain and parse JSON manually with this mapper so the snake_case ↔ camelCase
    // mappings on TmdbMovieDto et al. actually fire.
    private final ObjectMapper jsonMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

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

    /**
     * Fetch a TMDB endpoint as raw JSON, then deserialize with our Jackson 2.x
     * mapper. Bypasses RestTemplate's message converters entirely so we don't
     * pick up the Jackson 3.x converter (which ignores Jackson 2.x annotations).
     */
    private <T> T fetchJson(String url, Class<T> type) {
        String json = restTemplate.getForObject(url, String.class);
        if (json == null || json.isBlank()) return null;
        try {
            return jsonMapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse TMDB JSON for " + type.getSimpleName() + ": " + e.getMessage(), e
            );
        }
    }

    // Get genres public
    TmdbGenreResponseDto getGenres() {
        return fetchJson(tmdbGenreUrl, TmdbGenreResponseDto.class);
    }

    public TmdbMovieResponseDto getPopularMovies() {
        return fetchJson(tmdbPopularMoviesUrl + tmdbApiKey, TmdbMovieResponseDto.class);
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

        int maxPages = Math.max(10, (movieCount / 20) + 5);
        int page = 1;
        int consecutiveFailures = 0;
        String lastError = null;

        while (results.size() < movieCount && page <= maxPages) {
            TmdbMovieResponseDto response;
            try {
                response = fetchJson(
                        tmdbPopularMoviesUrl + tmdbApiKey + "&page=" + page,
                        TmdbMovieResponseDto.class
                );
                consecutiveFailures = 0;
            } catch (Exception e) {
                // Walk the cause chain so we can see the real Jackson error,
                // not just RestTemplate's wrapper "Error while extracting...".
                Throwable root = e;
                while (root.getCause() != null && root.getCause() != root) {
                    root = root.getCause();
                }
                lastError = "TMDB page " + page + " failed: "
                        + e.getClass().getSimpleName() + " — "
                        + (root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName());
                System.out.println(lastError);
                e.printStackTrace();
                consecutiveFailures++;
                if (consecutiveFailures >= 3) {
                    break;
                }
                page++;
                continue;
            }

            if (response == null || response.getResults() == null) {
                lastError = "TMDB page " + page + " returned no results";
                System.out.println(lastError);
                page++;
                continue;
            }

            for (TmdbMovieDto dto : response.getResults()) {
                if (dto.getId() == null) continue;
                if (!seen.add(dto.getId())) continue;
                results.add(dto);
                if (results.size() >= movieCount) break;
            }

            page++;
        }

        // If we couldn't fetch anything at all, surface the actual cause so the
        // ImportJob ends up FAILED with a useful errorMessage instead of a
        // silent "DONE / processedMovies = 0".
        if (results.isEmpty()) {
            throw new IllegalStateException(
                    "Could not fetch any movies from TMDB. " +
                            (lastError != null ? lastError : "Unknown reason — check API key and network.")
            );
        }

        return results;
    }

    private Integer getMovieRuntime(Long movieId) {
        String url = tmdbMovieDetails + movieId + "?api_key=" + tmdbApiKey;
        try {
            TmdbMovieDetailsDto movie = fetchJson(url, TmdbMovieDetailsDto.class);
            return movie != null ? movie.getRuntime() : null;
        } catch (Exception e) {
            // A missing runtime shouldn't fail the whole import.
            return null;
        }
    }
    // -------------------------
    // STEP 2: SAVE MOVIES ONLY
    // -------------------------
    @Transactional
    protected List<Movie> saveBasicMovies(List<TmdbMovieDto> dtos) {

        List<Movie> movies = new ArrayList<>();

        for (TmdbMovieDto dto : dtos) {
            try {
                Movie movie = movieRepository.findByTmdbId(dto.getId())
                        .orElseGet(Movie::new);

                movie.setTmdbId(dto.getId());
                movie.setTitle(dto.getTitle());
                movie.setOverview(dto.getOverview());
                movie.setReleaseDate(dto.getReleaseDate());
                movie.setLanguage(dto.getOriginalLanguage());
                movie.setAverageRating(dto.getVoteAverage());
                movie.setRuntimeMinutes(getMovieRuntime(dto.getId()));
                movie.setPosterPath(dto.getPosterPath());
                movie.setBackdropPath(dto.getBackdropPath());

                movies.add(movie);
            } catch (Exception e) {
                // A single bad movie shouldn't fail the whole batch.
                System.out.println(
                        "Skipping movie tmdbId=" + dto.getId() + ": " + e.getMessage()
                );
            }
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
        return fetchJson(url, TmdbCreditsResponseDto.class);
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