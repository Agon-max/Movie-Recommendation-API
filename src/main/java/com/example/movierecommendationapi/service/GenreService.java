package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.GenreDto;
import com.example.movierecommendationapi.dto.TmdbGenreDto;
import com.example.movierecommendationapi.entity.Genre;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.GenreMapper;
import com.example.movierecommendationapi.repository.GenreRepository;
import com.example.movierecommendationapi.wrapper.TmdbGenreResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final TmdbService tmdbService;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, TmdbService tmdbService, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.tmdbService = tmdbService;
        this.genreMapper = genreMapper;
    }

    public boolean genreExists(Long id) {
        return genreRepository.existsById(id);
    }

    public GenreDto getGenreById(Long id) {
        return genreRepository.findById(id)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new ResourceNotFound("Genre not found!"));
    }

    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    public GenreDto createGenre(GenreDto genreDto) {
        Genre genre = genreMapper.toEntity(genreDto);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDto(savedGenre);
    }

    @Transactional
    public GenreDto updateGenre(Long id, GenreDto genreDto) {
        Genre existingGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Genre not found!"));
        genreMapper.updateManagedGenre(genreDto, existingGenre);
        return genreMapper.toDto(existingGenre);
    }

    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFound("Genre not found!");
        }
        genreRepository.deleteById(id);
    }

    public TmdbGenreResponseDto importGenresIntoLocalDB() {

        var responseGenres = tmdbService.getGenres();

        if (responseGenres == null) {
            throw new ResourceNotFound("External genres not found!");
        }

        List<TmdbGenreDto> tmdbGenres = responseGenres.getGenres();

        for (TmdbGenreDto tmdbGenre : tmdbGenres) {

            GenreDto genreDto = new GenreDto();

            genreDto.setTmdbId(tmdbGenre.getId());
            genreDto.setTitle(tmdbGenre.getName());

            createGenre(genreDto);
        }

        return responseGenres;
    }
}