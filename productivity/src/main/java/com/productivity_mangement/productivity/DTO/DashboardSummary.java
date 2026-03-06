package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardSummary {

    private List<Task> dailyTasks;
    private List<Task> weeklyTasks;
    private List<Task> upcomingDeadlines;
    private List<Task> highPriorityTasks;
    private List<Task> aiSuggestedTasks;

    private Map<EisenhowerQuadrant, List<Task>> eisenhowerMatrix;

    private long totalTasks;
    private long completedTasks;

    private long unreadNotifications;
}

