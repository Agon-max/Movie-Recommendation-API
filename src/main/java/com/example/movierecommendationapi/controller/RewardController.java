package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.dto.CreateRewardDto;
import com.example.movierecommendationapi.dto.RewardDto;
import com.example.movierecommendationapi.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping
    @Operation(summary = "Create a new reward")
    public ResponseEntity<RewardDto> createReward(@Valid @RequestBody CreateRewardDto reward) {
        RewardDto createdReward = rewardService.createReward(reward);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReward);
    }

    @GetMapping
    @Operation(summary = "Get all rewards")
    public ResponseEntity<List<RewardDto>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reward by id")
    public ResponseEntity<RewardDto> getRewardById(@PathVariable Long id) {
        return ResponseEntity.ok(rewardService.getRewardById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reward")
    public ResponseEntity<RewardDto> updateReward(
            @PathVariable Long id,
            @Valid @RequestBody RewardDto updatedReward
    ) {
        return ResponseEntity.ok(rewardService.updateReward(id, updatedReward));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reward")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active rewards")
    public ResponseEntity<List<RewardDto>> getActiveRewards() {
        return ResponseEntity.ok(rewardService.getActiveRewards());
    }
}
