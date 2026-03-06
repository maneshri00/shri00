package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamsService {

    public List<Task> fetchTasks(String userEmail) {
        // TODO: Implement Microsoft Teams / Graph API integration
        return List.of();
    }
}

