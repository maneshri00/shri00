package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordService {

    public List<Task> fetchWorkTasks(String userEmail) {
        // TODO: Implement Discord OAuth2 + API integration for work servers
        return List.of();
    }

    public List<Task> fetchPersonalTasks(String userEmail) {
        // TODO: Implement Discord OAuth2 + API integration for personal servers
        return List.of();
    }
}

