package com.productivity_mangement.productivity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_behavior_metrics",
        indexes = {
                @Index(name = "idx_behavior_user_sender", columnList = "userEmail,sender")
        })
public class UserBehaviorMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String sender;

    private long seenCount;

    private long completedCount;

    private long ignoredCount;

    private LocalDateTime lastSeenAt;

    private LocalDateTime lastCompletedAt;

    private int senderImportanceIndex;
}

