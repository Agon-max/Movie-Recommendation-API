package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.PointEvent;
import com.example.movierecommendationapi.entity.User;
import com.example.movierecommendationapi.entity.UserPointHistory;
import com.example.movierecommendationapi.entity.enums.PointEventType;
import com.example.movierecommendationapi.repository.PointEventRepository;
import com.example.movierecommendationapi.repository.UserPointHistoryRepository;
import com.example.movierecommendationapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PointService {

    private final PointEventRepository eventRepo;
    private final UserRepository userRepo;
    private final UserPointHistoryRepository pointHistoryRepo;

    public PointService(PointEventRepository pointEventRepo, UserRepository userRepository, UserPointHistoryRepository userPointHistoryRepository){
        eventRepo = pointEventRepo;
        userRepo = userRepository;
        pointHistoryRepo = userPointHistoryRepository;
    }

    public void awardPoints(User user, PointEventType eventType) {
        PointEvent event = new PointEvent();
        event.setUser(user);
        int pointsToAward;

        if (!event.isActive()) {
            return;
        }


        switch (eventType) {

            case FIRST_LOGIN -> {
                if(user.isFirstLogin()) {
                    pointsToAward = 20;

                    user.setTotalPoints(
                            user.getTotalPoints() + pointsToAward
                    );


                    event.setPointsAwarded(pointsToAward);
                    event.setDescription(
                            "The user with username " + user.getUsername()
                                    + " has logged in for the first time!"
                    );
                    event.setEventType(eventType);
                    user.setFirstLogin(true);
                }

                return;
            }

            case WRITE_REVIEW -> {
                pointsToAward = 10;

                user.setTotalPoints(
                        user.getTotalPoints() + pointsToAward
                );

                event.setPointsAwarded(pointsToAward);
                event.setDescription(
                        "The user with username " + user.getUsername()
                                + " wrote a review!"
                );
                event.setEventType(eventType);

            }

            case WATCH_MOVIE -> {
                pointsToAward = 15;

                user.setTotalPoints(
                        user.getTotalPoints() + pointsToAward
                );

                event.setPointsAwarded(pointsToAward);
                event.setDescription(
                        "The user with username " + user.getUsername()
                                + " watched a movie!"
                );
                event.setEventType(eventType);

            }

            case REWARD_REDEMPTION_BONUS -> {
                pointsToAward = 5;

                user.setTotalPoints(
                        user.getTotalPoints() + pointsToAward
                );

                event.setPointsAwarded(pointsToAward);
                event.setDescription(
                        "The user with username " + user.getUsername()
                                + " has been granted a reward redemption bonus!"
                );
                event.setEventType(eventType);

            }
        }


        userRepo.save(user);

        UserPointHistory history = new UserPointHistory();

        history.setUser(user);
        history.setEventType(eventType);
        history.setPointsReceived(event.getPointsAwarded());
        history.setCreatedAt(LocalDateTime.now());

        pointHistoryRepo.save(history);
        eventRepo.save(event);
    }

}
