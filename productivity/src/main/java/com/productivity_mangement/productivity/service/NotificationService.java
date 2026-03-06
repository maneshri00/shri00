package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.entity.NotificationEntity;
import com.productivity_mangement.productivity.repository.NotificationRepository;
import com.productivity_mangement.productivity.controller.StreamController;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final StreamController streamController;

    public NotificationService(NotificationRepository repository,
                               StreamController streamController) {
        this.repository = repository;
        this.streamController = streamController;
    }

    public void createForTask(Task task) {
        if (task.getUserEmail() == null) return;

        NotificationEntity n = new NotificationEntity();
        n.setUserEmail(task.getUserEmail());
        n.setTaskId(task.getId());
        n.setTitle(task.getTitle() != null ? task.getTitle() : "New task");

        StringBuilder body = new StringBuilder();
        if (task.getDescription() != null) {
            body.append(task.getDescription());
        } else if (task.getContent() != null) {
            body.append(task.getContent());
        }

        n.setBody(body.toString());
        n.setType("TASK_CREATED");
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());

        NotificationEntity saved = repository.save(n);
        streamController.sendNotification(task.getUserEmail(), saved);
    }

    public List<NotificationEntity> getNotifications(String userEmail) {
        return repository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }

    public long getUnreadCount(String userEmail) {
        return repository.countByUserEmailAndReadIsFalse(userEmail);
    }

    public void markRead(String userEmail, Long id) {
        NotificationEntity n =
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!userEmail.equals(n.getUserEmail())) {
            throw new RuntimeException("Forbidden");
        }

        n.setRead(true);
        repository.save(n);
    }
}

