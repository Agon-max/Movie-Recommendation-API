package com.example.movierecommendationapi.repository.customRepos;

import com.example.movierecommendationapi.entity.Actor;

import java.util.List;

public interface ActorRepositoryCustom {

    public List<Actor> getActorsByMovie(Long movieId, String movieTitle);
}
