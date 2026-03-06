package com.productivity_mangement.productivity.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Task {
    private Integer manualPriorityBoost;
    private boolean fixed;
    @Id
    @GeneratedValue
    private Long id;
    private String externalId;

    private TaskSource source;

    private String title;
    private String content;

    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

    private TaskStatus status;  // TODO, IN_PROGRESS, DONE

    private int priorityScore;
    private List<String> reasons = new ArrayList<>();

    private LocalDateTime fixedStartTime;
    private LocalDateTime fixedEndTime;

    private Integer estimatedMinutes;
    private String userEmail;
    private String description;
    private String priority;
//    private String fixedStartTime;
//    private String fixedEndTime;

}
