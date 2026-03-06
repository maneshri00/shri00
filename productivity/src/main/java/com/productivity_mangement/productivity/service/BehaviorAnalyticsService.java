package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.entity.UserBehaviorMetric;
import com.productivity_mangement.productivity.repository.UserBehaviorMetricRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BehaviorAnalyticsService {

    private final UserBehaviorMetricRepository repository;

    public BehaviorAnalyticsService(UserBehaviorMetricRepository repository) {
        this.repository = repository;
    }

    public int recordSeenAndGetImportance(String userEmail, String sender) {
        if (userEmail == null || sender == null || sender.isBlank()) {
            return 0;
        }

        UserBehaviorMetric metric =
                repository.findByUserEmailAndSender(userEmail, sender)
                        .orElseGet(() -> {
                            UserBehaviorMetric m = new UserBehaviorMetric();
                            m.setUserEmail(userEmail);
                            m.setSender(sender);
                            m.setSeenCount(0);
                            m.setCompletedCount(0);
                            m.setIgnoredCount(0);
                            m.setSenderImportanceIndex(20);
                            return m;
                        });

        metric.setSeenCount(metric.getSeenCount() + 1);
        metric.setLastSeenAt(LocalDateTime.now());

        int importance = computeImportance(metric);
        metric.setSenderImportanceIndex(importance);

        repository.save(metric);

        return importance;
    }

    public void recordCompleted(String userEmail, String sender) {
        if (userEmail == null || sender == null || sender.isBlank()) {
            return;
        }

        UserBehaviorMetric metric =
                repository.findByUserEmailAndSender(userEmail, sender)
                        .orElseGet(() -> {
                            UserBehaviorMetric m = new UserBehaviorMetric();
                            m.setUserEmail(userEmail);
                            m.setSender(sender);
                            m.setSeenCount(0);
                            m.setCompletedCount(0);
                            m.setIgnoredCount(0);
                            m.setSenderImportanceIndex(20);
                            return m;
                        });

        metric.setCompletedCount(metric.getCompletedCount() + 1);
        metric.setLastCompletedAt(LocalDateTime.now());

        int importance = computeImportance(metric);
        metric.setSenderImportanceIndex(importance);

        repository.save(metric);
    }

    private int computeImportance(UserBehaviorMetric metric) {
        long seen = Math.max(1, metric.getSeenCount());
        long completed = metric.getCompletedCount();

        double completionRate = (double) completed / seen;

        int base = 20;
        int completionBoost = (int) (completionRate * 60);
        int volumeBoost = (int) Math.min(20, metric.getSeenCount() * 2L);

        int score = base + completionBoost + volumeBoost;

        if (score > 100) score = 100;
        if (score < 0) score = 0;

        return score;
    }
}

