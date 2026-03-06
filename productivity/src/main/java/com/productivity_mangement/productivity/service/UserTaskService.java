package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.entity.TaskEntity;
import com.productivity_mangement.productivity.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserTaskService {

    private final TaskRepository taskRepository;

    public UserTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void addTask(Task task) {

        TaskEntity e = new TaskEntity();

        e.setTitle(task.getTitle());
        e.setDescription(task.getContent());
        e.setUserEmail(task.getUserEmail());
        e.setStatus(task.getStatus());
        e.setFixed(false);

        taskRepository.save(e);
    }

    public List<Task> getTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deleteTask(Long id) {    // ✅ FIXED
        taskRepository.deleteById(id);
    }

    private Task toDto(TaskEntity e) {

        Task t = new Task();
        t.setId(e.getId());
        t.setTitle(e.getTitle());
        t.setContent(e.getDescription());
        t.setStatus(e.getStatus());
        return t;
    }
}
