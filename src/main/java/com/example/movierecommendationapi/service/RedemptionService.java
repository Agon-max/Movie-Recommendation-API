package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.*;
import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final UserPointHistoryRepository historyRepository;
    private final PointService pointService;
    private final UserRedemptionHistoryRepository redemptionHistoryRepo;

    public RedemptionService(
            RedemptionRepository redemptionRepository,
            RewardRepository rewardRepository,
            UserRepository userRepository,
            UserPointHistoryRepository historyRepository,
            PointService pointService,
            UserRedemptionHistoryRepository redemptionHistoryRepo
    ) {
        this.redemptionRepository = redemptionRepository;
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.pointService = pointService;
        this.redemptionHistoryRepo = redemptionHistoryRepo;
    }


    @Transactional
    public Redemption redeemReward(Long userId, Long rewardId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFound("User not found"));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() ->
                        new ResourceNotFound("Reward not found"));

        // -------------------------
        // VALIDATION
        // -------------------------
        if (!reward.isActive()) {
            throw new RuntimeException("Reward is not active");
        }

        if (user.getTotalPoints() < reward.getPointCost()) {
            throw new RuntimeException("Not enough points");
        }

        // -------------------------
        // DEDUCT POINTS
        // -------------------------
        user.setTotalPoints(
                user.getTotalPoints() - reward.getPointCost()
        );

        userRepository.save(user);

        // -------------------------
        // CREATE REDEMPTION
        // -------------------------

        boolean firstRedemption =
                !redemptionRepository.existsByUser(user);

        if (firstRedemption) {
            pointService.awardPoints(
                    user,
                    PointEventType.REWARD_REDEMPTION_BONUS
            );
        }

        Redemption redemption = new Redemption();

        redemption.setUser(user);
        redemption.setReward(reward);
        redemption.setPointsSpent(reward.getPointCost());
        redemption.setRedeemedAt(LocalDateTime.now());

        Redemption savedRedemption =
                redemptionRepository.save(redemption);

        UserRedemptionHistory redHistory = new UserRedemptionHistory();
        redHistory.setUser(user);
        redHistory.setReward(reward);
        redHistory.setReason("REDEEM_" + reward.getName());
        redHistory.setPointsSpent(reward.getPointCost());
        redHistory.setCreatedAt(LocalDateTime.now());

        redemptionHistoryRepo.save(redHistory);




        // -------------------------
        // ADD HISTORY ENTRY
        // -------------------------
        UserPointHistory pointHistory = new UserPointHistory();


        pointHistory.setUser(user);
        pointHistory.setPointsReceived(-reward.getPointCost());
        pointHistory.setEventType(PointEventType.REWARD_REDEMPTION_BONUS);
        pointHistory.setCreatedAt(LocalDateTime.now());

        historyRepository.save(pointHistory);

        return savedRedemption;
    }

    public List<Redemption> getUserRedemptions(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFound("User not found"));

        return redemptionRepository.findByUser(user);
    }
}