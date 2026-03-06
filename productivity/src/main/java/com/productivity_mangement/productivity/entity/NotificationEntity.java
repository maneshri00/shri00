package com.productivity_mangement.productivity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String title;

    @Column(length = 1000)
    private String body;

    private String type;

    private boolean read;

    private LocalDateTime createdAt;

    private Long taskId;
}

