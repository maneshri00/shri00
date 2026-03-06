package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutlookService {

    public List<Task> fetchMailTasks(String userEmail) {
        // TODO: Implement Outlook Mail integration via Microsoft Graph
        return List.of();
    }
}

