package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.entity.NotificationEntity;
import com.productivity_mangement.productivity.helper.SessionUser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final SessionUser sessionUser;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> heartbeats = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public StreamController(SessionUser sessionUser) {
        this.sessionUser = sessionUser;
    }

    @GetMapping(path = "/dashboard", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamDashboard() {
        String email = sessionUser.getEmail();
        if (email == null) {
            throw new RuntimeException("User not in session");
        }

        SseEmitter emitter = new SseEmitter(Duration.ofMinutes(30).toMillis());
        emitters.put(email, emitter);

        Runnable beat = () -> {
            SseEmitter em = emitters.get(email);
            if (em == null) return;
            try {
                em.send(SseEmitter.event().name("ping").data("keepalive"));
            } catch (IOException ignored) {
                emitters.remove(email);
            }
        };
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(beat, 10, 20, TimeUnit.SECONDS);
        heartbeats.put(email, future);

        Runnable cleanup = () -> {
            emitters.remove(email);
            ScheduledFuture<?> f = heartbeats.remove(email);
            if (f != null) f.cancel(true);
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            emitter.send(SseEmitter.event().name("init").data("connected"));
        } catch (IOException ignored) {
        }

        return emitter;
    }

    public void sendTaskUpdate(String userEmail, Task task) {
        SseEmitter emitter = emitters.get(userEmail);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name("task-updated")
                    .data(task));
        } catch (IOException e) {
            emitters.remove(userEmail);
        }
    }

    public void sendNotification(String userEmail, NotificationEntity notification) {
        SseEmitter emitter = emitters.get(userEmail);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
        } catch (IOException e) {
            emitters.remove(userEmail);
        }
    }
}

