package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.TaskStatus;
import com.productivity_mangement.productivity.entity.TaskEntity;
import com.productivity_mangement.productivity.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public void markTaskDone(Long id) {

        TaskEntity task =
                repo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(TaskStatus.DONE);
        repo.save(task);
    }
}

