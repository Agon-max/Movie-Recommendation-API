package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.Reward;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public Reward createReward(Reward reward) {

        return rewardRepository.save(reward);
    }

    public List<Reward> getAllRewards() {

        return rewardRepository.findAll();
    }

    public Reward getRewardById(Long id) {

        return rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));
    }

    public Reward updateReward(Long id, Reward updatedReward) {

        Reward existingReward = rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));

        existingReward.setName(updatedReward.getName());
        existingReward.setDescription(updatedReward.getDescription());
        existingReward.setPointCost(updatedReward.getPointCost());
        existingReward.setType(updatedReward.getType());
        existingReward.setMonetaryValue(updatedReward.getMonetaryValue());
        existingReward.setActive(updatedReward.isActive());
        existingReward.setStock(updatedReward.getStock());

        return rewardRepository.save(existingReward);
    }


    public void deleteReward(Long id) {

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));

        rewardRepository.delete(reward);
    }


    public List<Reward> getActiveRewards() {

        return rewardRepository.findByActiveTrue();
    }
}