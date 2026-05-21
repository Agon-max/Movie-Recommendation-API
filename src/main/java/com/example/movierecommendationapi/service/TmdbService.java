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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
public class TmdbService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final GenreRepository genreRepository;
    private final ImportJobRepository importJobRepository;

    // We use java.net.http.HttpClient directly instead of RestTemplate so we
    // don't have to fight Spring Boot 4's message-converter chain (the Jackson
    // converter sits at position 0 and tries to deserialize {...} into a String
    // when getForObject(url, String.class) is called, which blows up with
    // "Error while extracting response for type [class java.lang.String]").
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Dedicated Jackson 2.x mapper — Spring Boot 4 uses Jackson 3.x by default
    // (tools.jackson.*), which doesn't recognise our @JsonProperty annotations
    // from com.fasterxml.jackson.annotation.
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
            MovieRepository movieRepository,
            ActorRepository actorRepository,
            DirectorRepository directorRepository,
            GenreRepository genreRepository,
            ImportJobRepository importJobRepository
    ) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.directorRepository = directorRepository;
        this.genreRepository = genreRepository;
        this.importJobRepository = importJobRepository;
    }

    private <T> T fetchJson(String url, Class<T> type) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                throw new IllegalStateException(
                        "TMDB HTTP " + resp.statusCode() + " for " + url
                                + (resp.body() != null ? " — " + resp.body() : "")
                );
            }
            String json = resp.body();
            if (json == null || json.isBlank()) return null;
            return jsonMapper.readValue(json, type);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to fetch/parse TMDB JSON for " + type.getSimpleName() + ": " + e.getMessage(), e
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

        // Fetch the TMDB master genre list once so per-movie genre resolution
        // doesn't hammer the API and we have names available for any
        // genre_ids that aren't in our DB yet.
        TmdbGenreResponseDto tmdbGenreList = getGenres();

        List<TmdbMovieDto> collectedMovies =
                fetchPopularMovies(movieCount);

        Map<Long, TmdbMovieDto> dtoByTmdbId = new HashMap<>();
        for (TmdbMovieDto dto : collectedMovies) {
            if (dto.getId() != null) {
                dtoByTmdbId.put(dto.getId(), dto);
            }
        }

        List<Movie> savedMovies =
                saveBasicMovies(collectedMovies);

        enrichMovies(savedMovies, dtoByTmdbId, tmdbGenreList, job);
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
    // Per-movie save so a single bad row (e.g. overview > column length)
    // doesn't tank the whole batch the way saveAll would.
    private List<Movie> saveBasicMovies(List<TmdbMovieDto> dtos) {

        List<Movie> saved = new ArrayList<>();

        for (TmdbMovieDto dto : dtos) {
            // Hard skip: adult-flagged movies never enter our DB.
            if (dto.isAdult()) {
                continue;
            }
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
                movie.setAdult(false);

                saved.add(movieRepository.save(movie));
            } catch (Exception e) {
                System.out.println(
                        "Skipping movie tmdbId=" + dto.getId() + ": " + e.getMessage()
                );
            }
        }

        return saved;
    }

    private void enrichMovies(
            List<Movie> movies,
            Map<Long, TmdbMovieDto> dtoByTmdbId,
            TmdbGenreResponseDto tmdbGenreList,
            ImportJob job
    ) {

        for (Movie movie : movies) {

            try {

                TmdbCreditsResponseDto credits =
                        fetchCredits(movie.getTmdbId());

                if (credits == null)
                    continue;

                TmdbMovieDto sourceDto = dtoByTmdbId.get(movie.getTmdbId());
                List<Long> genreIds = sourceDto != null ? sourceDto.getGenreIds() : null;

                movie.setActors(importActors(credits));
                movie.setDirectors(importDirectors(credits));
                movie.setGenres(resolveGenres(genreIds, tmdbGenreList));

                movieRepository.save(movie);

            } catch (Exception e) {

                System.out.println(
                        "Failed movie: " + movie.getTmdbId() + " — " + e.getMessage()
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
    // Resolve a movie's specific genre_ids to Genre entities.
    // - Existing genres are loaded from the DB.
    // - Unknown genre_ids are persisted on the fly (using the TMDB master
    //   list for the human-readable name).
    // - Movie.genres has no cascade, so genres MUST be persisted before
    //   being attached to the movie or the FK insert into movie_genres fails.
    private List<Genre> resolveGenres(
            List<Long> tmdbGenreIds,
            TmdbGenreResponseDto tmdbGenreList
    ) {
        List<Genre> resolved = new ArrayList<>();
        if (tmdbGenreIds == null || tmdbGenreIds.isEmpty()) return resolved;

        Map<Long, String> nameByTmdbId = new HashMap<>();
        if (tmdbGenreList != null && tmdbGenreList.getGenres() != null) {
            for (TmdbGenreDto g : tmdbGenreList.getGenres()) {
                if (g.getId() != null) {
                    nameByTmdbId.put(g.getId(), g.getName());
                }
            }
        }

        Set<Long> seen = new HashSet<>();
        for (Long tmdbGenreId : tmdbGenreIds) {
            if (tmdbGenreId == null || !seen.add(tmdbGenreId)) continue;

            Genre genre = genreRepository.findByTmdbId(tmdbGenreId)
                    .orElseGet(() -> {
                        Genre g = new Genre();
                        g.setTmdbId(tmdbGenreId);
                        g.setTitle(nameByTmdbId.getOrDefault(tmdbGenreId, "Unknown"));
                        return genreRepository.save(g);
                    });
            resolved.add(genre);
        }
        return resolved;
    }
}