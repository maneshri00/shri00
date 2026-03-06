package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.*;
import com.productivity_mangement.productivity.helper.SessionUser;
import com.productivity_mangement.productivity.service.NotificationService;
import com.productivity_mangement.productivity.service.TaskAggregationService;
import com.productivity_mangement.productivity.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final TaskAggregationService taskAggregationService;
    private final UserProfileService userProfileService;
    private final NotificationService notificationService;
    private final SessionUser sessionUser;

    public DashboardController(
            TaskAggregationService taskAggregationService,
            UserProfileService userProfileService,
            NotificationService notificationService,
            SessionUser sessionUser
    ) {
        this.taskAggregationService = taskAggregationService;
        this.userProfileService = userProfileService;
        this.notificationService = notificationService;
        this.sessionUser = sessionUser;
    }

    @GetMapping("/professional")
    public DashboardSummary professional() throws IOException {
        return buildDashboard(TaskScope.PROFESSIONAL);
    }

    @GetMapping("/personal")
    public DashboardSummary personal() throws IOException {
        return buildDashboard(TaskScope.PERSONAL);
    }

    private DashboardSummary buildDashboard(TaskScope scope) throws IOException {
        String email = sessionUser.getEmail();

        UserGoalProfile profile = userProfileService.buildGoalProfile(email);
        List<Task> prioritized = taskAggregationService.getPrioritizedTasks(profile, email)
                .stream()
                .filter(t -> t.getScope() == scope)
                .toList();

        DashboardSummary summary = new DashboardSummary();

        LocalDate today = LocalDate.now();

        List<Task> daily = prioritized.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().toLocalDate().isEqual(today))
                .sorted(Comparator.comparingInt(Task::getFinalPriorityScore).reversed())
                .toList();

        List<Task> weekly = prioritized.stream()
                .filter(t -> t.getDueDate() != null
                        && !t.getDueDate().toLocalDate().isBefore(today)
                        && !t.getDueDate().toLocalDate().isAfter(today.plusDays(7)))
                .sorted(Comparator.comparingInt(Task::getFinalPriorityScore).reversed())
                .toList();

        List<Task> upcomingDeadlines = prioritized.stream()
                .filter(t -> t.getDueDate() != null)
                .sorted(Comparator.comparing(t -> t.getDueDate()))
                .limit(20)
                .toList();

        List<Task> highPriority = prioritized.stream()
                .filter(t -> t.getFinalPriorityScore() >= 80)
                .sorted(Comparator.comparingInt(Task::getFinalPriorityScore).reversed())
                .limit(20)
                .toList();

        List<Task> aiSuggested = prioritized.stream()
                .sorted(Comparator.comparingInt(Task::getFinalPriorityScore).reversed())
                .limit(20)
                .toList();

        Map<EisenhowerQuadrant, List<Task>> matrix = new EnumMap<>(EisenhowerQuadrant.class);
        for (EisenhowerQuadrant q : EisenhowerQuadrant.values()) {
            matrix.put(q,
                    prioritized.stream()
                            .filter(t -> q.equals(t.getQuadrant()))
                            .collect(Collectors.toList()));
        }

        summary.setDailyTasks(daily);
        summary.setWeeklyTasks(weekly);
        summary.setUpcomingDeadlines(upcomingDeadlines);
        summary.setHighPriorityTasks(highPriority);
        summary.setAiSuggestedTasks(aiSuggested);
        summary.setEisenhowerMatrix(matrix);

        summary.setTotalTasks(prioritized.size());
        summary.setCompletedTasks(
                prioritized.stream()
                        .filter(t -> t.getStatus() == TaskStatus.DONE)
                        .count()
        );

        summary.setUnreadNotifications(
                notificationService.getUnreadCount(email)
        );

        return summary;
    }
}

