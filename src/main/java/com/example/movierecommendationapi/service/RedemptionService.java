package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.repository.RedemptionRepository;
import org.springframework.stereotype.Service;

@Service
public class RedemptionService {

    private final RedemptionRepository redemptionRepository;

    public RedemptionService(RedemptionRepository redemptionRepository) {
        this.redemptionRepository = redemptionRepository;
    }


}
