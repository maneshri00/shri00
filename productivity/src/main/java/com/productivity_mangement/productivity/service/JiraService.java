package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JiraService {

    public List<Task> fetchTasks(String userEmail) {
        // TODO: Implement JIRA OAuth2 + REST integration
        return List.of();
    }
}

