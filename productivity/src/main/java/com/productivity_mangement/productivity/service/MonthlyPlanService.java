package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.MonthlyPlan;
import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonthlyPlanService {

    public MonthlyPlan generate(
            List<Task> tasks
    ) {
        MonthlyPlan plan = new MonthlyPlan();

        for (Task task : tasks) {
            if (task.getPriorityScore() >= 70) {
                plan.addHighImpactTask(task);
            }
        }

        return plan;
    }
}

