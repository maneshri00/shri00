package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import com.productivity_mangement.productivity.service.TaskAggregationService;
import com.productivity_mangement.productivity.service.UserGoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/priority")
public class PriorityController {

    private final TaskAggregationService aggregationService;
    private final UserGoalService userGoalService;

    public PriorityController(
            TaskAggregationService aggregationService,
            UserGoalService userGoalService
    ) {
        this.aggregationService = aggregationService;
        this.userGoalService = userGoalService;
    }

    @GetMapping("/tasks")
    public List<Task> getPriorityTasks(HttpServletRequest request) throws IOException {

        String email =
                (String) request.getSession().getAttribute("email");

        if (email == null) {
            return List.of();
        }

        UserGoalProfile user;
        try {
            user = userGoalService.getCurrentProfile();
        } catch (RuntimeException e) {
            return List.of();
        }
        return aggregationService.getPrioritizedTasks(user, email);
    }
}
