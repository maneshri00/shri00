package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StructuredTaskDTO {
    private String taskTitle;
    private String description;
    private LocalDateTime deadline;
    private String sender;
    private String sourcePlatform;
    private Integer urgencyScore;      // 0-100
    private Integer importanceScore;   // 0-100
    private String suggestedPriority;
    private List<String> tags;
    private Integer estimatedTimeMinutes;
}

