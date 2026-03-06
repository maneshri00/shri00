package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import com.productivity_mangement.productivity.entity.TaskEntity;
import com.productivity_mangement.productivity.service.TaskAggregationService;
import com.productivity_mangement.productivity.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskAggregationService taskService;
    private final UserProfileService profileService;

    public TaskController(
            TaskAggregationService taskService,
            UserProfileService profileService
    ) {
        this.taskService = taskService;
        this.profileService = profileService;
    }

    @GetMapping("/priority")
    public List<Task> getPriorityTasks(HttpServletRequest request)
            throws IOException {

        String email =
                (String) request.getSession().getAttribute("email");

        UserGoalProfile profile =
                profileService.buildGoalProfile(email);

        return taskService.getPrioritizedTasks(profile, email);
    }

    @PostMapping
    public Task addTask(
            @RequestBody Task task,
            HttpServletRequest request
    ) {

        String email =
                (String) request.getSession().getAttribute("email");

        task.setUserEmail(email);

        return taskService.saveTask(task);
    }

    @PutMapping("/{id}/complete")
    public void markDone(@PathVariable Long id) {
        taskService.markTaskDone(id);
    }

    @GetMapping("/completed")
    public List<TaskEntity> completedTasks(HttpServletRequest request) {

        String email =
                (String) request.getSession().getAttribute("email");

        return taskService.getCompletedTasks(email);
    }
}
