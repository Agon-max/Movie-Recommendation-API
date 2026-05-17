package com.example.movierecommendationapi.controller;

import com.example.movierecommendationapi.entity.Redemption;
import com.example.movierecommendationapi.service.RedemptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redemptions")
@Tag(name = "Redemption Controller", description = "Reward redemption endpoints")
public class RedemptionController {

    private final RedemptionService redemptionService;

    public RedemptionController(RedemptionService redemptionService) {
        this.redemptionService = redemptionService;
    }

    @PostMapping
    @Operation(summary = "Redeem a reward")
    public ResponseEntity<Redemption> redeemReward(
            @RequestParam Long userId,
            @RequestParam Long rewardId
    ) {

        Redemption redemption =
                redemptionService.redeemReward(userId, rewardId);

        return ResponseEntity.ok(redemption);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get redemption history for user")
    public ResponseEntity<List<Redemption>> getUserRedemptions(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                redemptionService.getUserRedemptions(userId)
        );
    }
}