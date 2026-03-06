package com.productivity_mangement.productivity.repository;

import com.productivity_mangement.productivity.entity.UserBehaviorMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBehaviorMetricRepository
        extends JpaRepository<UserBehaviorMetric, Long> {

    Optional<UserBehaviorMetric> findByUserEmailAndSender(
            String userEmail,
            String sender
    );
}

