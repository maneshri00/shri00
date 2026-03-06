package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.TaskSource;
import com.productivity_mangement.productivity.DTO.TaskStatus;
import com.productivity_mangement.productivity.DTO.UserTaskRequest;
import com.productivity_mangement.productivity.service.UserTaskService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user/tasks")
public class UserTaskController {

    private final UserTaskService userTaskService;

    public UserTaskController(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    @PostMapping
    public Task addTask(@RequestBody UserTaskRequest req) {

        Task task = new Task();

        task.setId(null);
        task.setSource(TaskSource.MANUAL);
        task.setTitle(req.getTitle());
        task.setContent(req.getContent());
        task.setDueDate(req.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.TODO);

        userTaskService.addTask(task);
        return task;
    }

    @GetMapping
    public List<Task> getTasks() {
        return userTaskService.getTasks();
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        userTaskService.deleteTask(id);
    }
}
