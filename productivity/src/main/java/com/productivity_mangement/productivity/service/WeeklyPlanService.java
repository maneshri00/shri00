package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.WeeklyPlan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeeklyPlanService {

    private final DailyPlanService dailyPlanService;

    public WeeklyPlanService(DailyPlanService dailyPlanService) {
        this.dailyPlanService = dailyPlanService;
    }

    public WeeklyPlan generate(
            List<Task> tasks,
            LocalDate weekStart
    ) {
        WeeklyPlan plan = new WeeklyPlan();

        for (int i = 0; i < 7; i++) {

            LocalDate day = weekStart.plusDays(i);

            LocalDateTime dayStart = day.atTime(9, 0);
            LocalDateTime dayEnd   = day.atTime(22, 0);

            plan.addDailyPlan(
                    dailyPlanService.generate(tasks, dayStart, dayEnd)
            );
        }

        return plan;
    }
}
