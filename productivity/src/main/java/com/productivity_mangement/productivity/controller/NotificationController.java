package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.entity.NotificationEntity;
import com.productivity_mangement.productivity.helper.SessionUser;
import com.productivity_mangement.productivity.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SessionUser sessionUser;

    public NotificationController(
            NotificationService notificationService,
            SessionUser sessionUser
    ) {
        this.notificationService = notificationService;
        this.sessionUser = sessionUser;
    }

    @GetMapping
    public List<NotificationEntity> list() {
        String email = sessionUser.getEmail();
        return notificationService.getNotifications(email);
    }

    @GetMapping("/summary")
    public long unread() {
        String email = sessionUser.getEmail();
        return notificationService.getUnreadCount(email);
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        String email = sessionUser.getEmail();
        notificationService.markRead(email, id);
    }
}

