package com.productivity_mangement.productivity.entity;

import com.productivity_mangement.productivity.DTO.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String priority;

    private Integer estimatedMinutes;

    private boolean fixed;

    private LocalTime fixedStartTime;

    private LocalTime fixedEndTime;

    private String userEmail;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;


}

