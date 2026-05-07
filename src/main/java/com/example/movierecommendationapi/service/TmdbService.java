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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

//    // Import movies, actors, directors, and associate genres
//    public void importMoviesActorsDirectorsGenres(int movieCount) {
//        Set<Long> processedMovieIds = new HashSet<>();
//        Set<Long> importedActorIds = new HashSet<>();
//        Set<Long> importedDirectorIds = new HashSet<>();
//        int page = 1;
//        int importedMovies = 0;
//
//        while (importedMovies < movieCount && page <= 10) { // Limit pages
//            TmdbMovieResponseDto moviesResponse = getPopularMovies(page);
//            for (TmdbMovieDto movieDto : moviesResponse.getResults()) {
//                if (processedMovieIds.add(movieDto.getId())) { // Avoid duplicates
//                    // Save movie (assume MovieService.createMovie exists)
//                    MovieDto movie = new MovieDto();
//                    movie.setTmdbId(movieDto.getId());
//                    movie.setTitle(movieDto.getTitle());
//                    // ... set other fields ...
//                    MovieDto savedMovie = movieService.createMovie(movie);
//
//                    // Associate genres
//                    for (Long genreId : movieDto.getGenreIds()) {
//                        Genre genre = genreRepository.findByTmdbId(genreId)
//                                .orElseThrow(() -> new RuntimeException("Genre not found: " + genreId));                        if (genre != null) {
//                            savedMovie.getGenres().add(genreMapper.toDto(genre));
//                        }
//                    }
//                    movieService.updateMovie(savedMovie.getId(), savedMovie);
//
//                    // Get credits
//                    TmdbCreditsResponseDto credits = getMovieCredits(movieDto.getId());
//
//                    // Import cast (actors)
//                    for (TmdbActorDto actorDto : credits.getCast()) {
//                        if (importedActorIds.add(actorDto.getId())) {
//                            ActorDto actor = new ActorDto();
//                            actor.setTmdbId(actorDto.getId());
//                            actor.setName(actorDto.getName());
//                            actorService.createActor(actor);
//                        }
//                    }
//
//                    // Import crew (directors, etc.)
//                    for (TmdbCrewMemberDto crewDto : credits.getCrew()) {
//                        if ("Director".equals(crewDto.getJob()) && importedDirectorIds.add(crewDto.getId())) {
//                            DirectorDto director = new DirectorDto();
//                            director.setTmdbId(crewDto.getId());
//                            director.setName(crewDto.getName());
//                            directorService.createDirector(director); // Assume exists
//                        }
//                    }
//
//                    importedMovies++;
//                    if (importedMovies >= movieCount) break;
//                }
//            }
//            page++;
//        }
//    }
//

        // Main import method (public)
        public void importExternalTMDBInfo(int movieCount)
        {
            Set<Long> processedMovieIds = new HashSet<>();
            Set<Long> importedActorIds = new HashSet<>();
            Set<Long> importedDirectorIds = new HashSet<>();
            int page = 1;
            int importedMovies = 0;

            while (importedMovies < movieCount && page <= 10) {
                TmdbMovieResponseDto moviesResponse = getPopularMovies(page);
                for (TmdbMovieDto movieDto : moviesResponse.getResults()) {
                    if (processedMovieIds.add(movieDto.getId())) {
                        // Import movie
                        MovieDto savedMovie = importMovie(movieDto);

                        // Import and associate genres
                        importGenresForMovie(savedMovie, movieDto.getGenreIds());

                        // Get credits and import actors/directors (pass savedMovie)
                        TmdbCreditsResponseDto credits = getMovieCredits(movieDto.getId());
                        try {
                            Thread.sleep(250);  // Delay to avoid rate limits
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        if (credits == null) {
                            throw new RuntimeException("Credits not found for movie: " + movieDto.getId());
                        }
                        importActorsFromCredits(credits, importedActorIds, savedMovie);  // Pass movie
                        importDirectorsFromCredits(credits, importedDirectorIds, savedMovie);  // Pass movie

                        // Update movie with new associations
                        movieService.updateMovie(savedMovie.getId(), savedMovie);

                        importedMovies++;
                        if (importedMovies >= movieCount) break;
                    }
                }
                page++;
            }
        }


    // Private method to import a single movie
    private MovieDto importMovie(TmdbMovieDto movieDto) {
        Optional<Movie> existingMovieOpt = movieRepository.findByTmdbId(movieDto.getId());
        if (existingMovieOpt.isPresent()) {
            return movieService.getMovieById(existingMovieOpt.get().getId());
        }
        MovieDto movie = new MovieDto();
        movie.setTmdbId(movieDto.getId());
        movie.setTitle(movieDto.getTitle());
        movie.setOverview(movieDto.getOverview() != null ? movieDto.getOverview() : "");
        movie.setLanguage(movieDto.getOriginalLanguage() != null ? movieDto.getOriginalLanguage() : "");

        if (movieDto.getReleaseDate() != null && !movieDto.getReleaseDate().isEmpty()) {
            try {
                movie.setReleaseDate(LocalDateTime.parse(movieDto.getReleaseDate() + "T00:00:00"));
            } catch (Exception e) {
                System.out.println("Warning: Invalid release date for movie " + movieDto.getId() + ": " + movieDto.getReleaseDate());
                movie.setReleaseDate(null);
            }
        }

        movie.setAverageRating(movieDto.getVoteAverage());
        return movieService.createMovie(movie);
    }


    // Private method to import and associate genres for a movie
    private void importGenresForMovie(MovieDto movie, List<Long> genreIds) {
        for (Long genreId : genreIds) {
            Genre genre = genreRepository.findByTmdbId(genreId).orElse(null);

            if (genre != null) {
                movie.getGenres().add(genreMapper.toDto(genre));
            }else{
                throw new RuntimeException("Genre not found: " + genreId);
            }
        }
        movieService.updateMovie(movie.getId(), movie);
    }

    // Private method to import actors from credits (add MovieDto param)
    private void importActorsFromCredits(TmdbCreditsResponseDto credits, Set<Long> importedActorIds, MovieDto movie) {
        if (credits.getCast() == null) {
            System.out.println("Warning: Cast not found for movie: " + credits.getId());
            return;
        }
        for (TmdbActorDto actorDto : credits.getCast()) {
            if (importedActorIds.add(actorDto.getId())) {
                // Check if exists
                Optional<Actor> existing = actorRepository.findByTmdbId(actorDto.getId());
                ActorDto savedActor;
                if (existing.isPresent()) {
                    savedActor = actorMapper.toDto(existing.get());
                } else {
                    ActorDto newActor = new ActorDto();
                    newActor.setTmdbId(actorDto.getId());
                    newActor.setName(actorDto.getName());
                    savedActor = actorService.createActor(newActor);
                }
                movie.getActors().add(savedActor);
            }
        }
    }


    // Private method to import directors from credits (add MovieDto param)
    private void importDirectorsFromCredits(TmdbCreditsResponseDto credits, Set<Long> importedDirectorIds, MovieDto movie) {
        if (credits.getCrew() == null) {
            System.out.println("Warning: Crew not found for movie: " + credits.getId());
            return;
        }
        for (TmdbCrewMemberDto crewDto : credits.getCrew()) {
            if ("Director".equals(crewDto.getJob()) && importedDirectorIds.add(crewDto.getId())) {
                // Check if exists (add this)
                Optional<Director> existing = directorRepository.findByTmdbId(crewDto.getId());
                DirectorDto savedDirector;
                if (existing.isPresent()) {
                    savedDirector = directorMapper.toDto(existing.get());
                } else {
                    DirectorDto newDirector = new DirectorDto();
                    newDirector.setTmdbId(crewDto.getId());
                    newDirector.setName(crewDto.getName());
                    savedDirector = directorService.createDirector(newDirector);
                }
                movie.getDirectors().add(savedDirector);  // Associate
            }
        }
    }

}


