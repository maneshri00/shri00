package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubService {

    public List<Task> fetchTasks(String userEmail) {
        // TODO: Implement GitHub OAuth2 + REST integration (issues, PRs)
        return List.of();
    }
}

