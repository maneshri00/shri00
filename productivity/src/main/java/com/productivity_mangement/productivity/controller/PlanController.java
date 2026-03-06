package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.DailyPlan;
import com.productivity_mangement.productivity.DTO.WeeklyPlan;
import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import com.productivity_mangement.productivity.DTO.UserProfileRequest;
import com.productivity_mangement.productivity.helper.SessionUser;
import com.productivity_mangement.productivity.service.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final TaskAggregationService taskAggregationService;
    private final DailyPlanService dailyPlanService;
    private final WeeklyPlanService weeklyPlanService;
    private final UserProfileService userProfileService;
    private final SessionUser sessionUser;

    public PlanController(
            TaskAggregationService taskAggregationService,
            DailyPlanService dailyPlanService,
            WeeklyPlanService weeklyPlanService,
            UserProfileService userProfileService,
            SessionUser sessionUser
    ) {
        this.taskAggregationService = taskAggregationService;
        this.dailyPlanService = dailyPlanService;
        this.weeklyPlanService = weeklyPlanService;
        this.userProfileService = userProfileService;
        this.sessionUser = sessionUser;
    }

    // ================= DAILY PLAN =================

    @GetMapping("/daily")
    public DailyPlan daily() throws Exception {

        String email = sessionUser.getEmail();

        UserProfileRequest profile =
                userProfileService.getProfile(email);

        LocalDate today = LocalDate.now();

        LocalDateTime start =
                LocalDateTime.of(
                        today,
                        LocalTime.parse(profile.getWakeTime())
                );

        LocalDateTime end =
                LocalDateTime.of(
                        today,
                        LocalTime.parse(profile.getSleepTime())
                );

        UserGoalProfile goalProfile =
                userProfileService.buildGoalProfile(email);

        var tasks =
                taskAggregationService
                        .getPrioritizedTasks(goalProfile, email);

        return dailyPlanService.generate(tasks, start, end);
    }



    @GetMapping("/weekly")
    public WeeklyPlan weekly(
            @RequestParam(required = false) String start
    ) throws Exception {

        String email = sessionUser.getEmail();

        UserGoalProfile goalProfile =
                userProfileService.buildGoalProfile(email);

        LocalDate weekStart =
                (start == null)
                        ? LocalDate.now()
                        : LocalDate.parse(start);

        var tasks =
                taskAggregationService
                        .getPrioritizedTasks(goalProfile, email);

        return weeklyPlanService.generate(tasks, weekStart);
    }
}
