package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.dto.RewardDto;
import com.example.movierecommendationapi.entity.Reward;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.mapper.RewardMapper;
import com.example.movierecommendationapi.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardMapper rewardMapper;

    public RewardService(RewardRepository rewardRepository, RewardMapper rewardMapper) {
        this.rewardRepository = rewardRepository;
        this.rewardMapper = rewardMapper;
    }

    public RewardDto createReward(RewardDto rewardDto) {
        Reward reward = rewardMapper.toEntity(rewardDto);
        Reward savedReward = rewardRepository.save(reward);
        return rewardMapper.toDto(savedReward);
    }

    public List<RewardDto> getAllRewards() {
        return rewardRepository.findAll().stream()
                .map(rewardMapper::toDto)
                .collect(Collectors.toList());
    }

    public RewardDto getRewardById(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));
        return rewardMapper.toDto(reward);
    }

    public RewardDto updateReward(Long id, RewardDto updatedRewardDto) {
        Reward existingReward = rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));

        // Update fields from DTO to entity
        existingReward.setName(updatedRewardDto.getName());
        existingReward.setDescription(updatedRewardDto.getDescription());
        existingReward.setPointCost(updatedRewardDto.getPointCost());
        existingReward.setType(updatedRewardDto.getType());
        existingReward.setMonetaryValue(updatedRewardDto.getMonetaryValue());
        existingReward.setActive(updatedRewardDto.isActive());
        existingReward.setStock(updatedRewardDto.getStock());

        Reward savedReward = rewardRepository.save(existingReward);
        return rewardMapper.toDto(savedReward);
    }


    public void deleteReward(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found with id: " + id));
        rewardRepository.delete(reward);
    }


    public List<RewardDto> getActiveRewards() {
        return rewardRepository.findByActiveTrue().stream()
                .map(rewardMapper::toDto)
                .collect(Collectors.toList());
    }
}