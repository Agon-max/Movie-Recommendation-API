package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.RewardDto;
import com.example.movierecommendationapi.entity.Reward;
import com.example.movierecommendationapi.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@Tag(name = "Reward Controller", description = "Reward management endpoints")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    // =========================
    // CREATE REWARD
    // =========================
    @PostMapping
    @Operation(summary = "Create a new reward")
    public ResponseEntity<RewardDto> createReward(
            @RequestBody RewardDto reward
    ) {
        RewardDto createdReward = rewardService.createReward(
                reward);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReward);
    }

    // =========================
    // GET ALL REWARDS
    // =========================
    @GetMapping
    @Operation(summary = "Get all rewards")
    public ResponseEntity<List<RewardDto>> getAllRewards() {

        List<RewardDto> rewards = rewardService.getAllRewards();

        return ResponseEntity.ok(rewards);
    }

    // =========================
    // GET REWARD BY ID
    // =========================
    @GetMapping("/{id}")
    @Operation(summary = "Get reward by id")
    public ResponseEntity<RewardDto> getRewardById(
            @PathVariable Long id
    ) {
        RewardDto reward = rewardService.getRewardById(id);

        return ResponseEntity.ok(reward);
    }

    // =========================
    // UPDATE REWARD
    // =========================
    @PutMapping("/{id}")
    @Operation(summary = "Update reward")
    public ResponseEntity<RewardDto> updateReward(
            @PathVariable Long id,
            @RequestBody RewardDto updatedReward
    ) {
        RewardDto reward = rewardService.updateReward(id, updatedReward);

        return ResponseEntity.ok(reward);
    }

    // =========================
    // DELETE REWARD
    // =========================
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reward")
    public ResponseEntity<String> deleteReward(
            @PathVariable Long id
    ) {
        rewardService.deleteReward(id);

        return ResponseEntity.ok("Reward deleted successfully");
    }

    // =========================
    // GET ACTIVE REWARDS
    // =========================
    @GetMapping("/active")
    @Operation(summary = "Get all active rewards")
    public ResponseEntity<List<RewardDto>> getActiveRewards() {

        List<RewardDto> rewards = rewardService.getActiveRewards();

        return ResponseEntity.ok(rewards);
    }
}