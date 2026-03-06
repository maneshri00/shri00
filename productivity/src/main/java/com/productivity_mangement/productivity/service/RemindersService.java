package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemindersService {

    public List<Task> fetchAppleReminders(String userEmail) {
        // TODO: Implement Apple Reminders integration (likely device- or iCloud-based)
        return List.of();
    }

    public List<Task> fetchGoogleReminders(String userEmail) {
        // TODO: Implement Google Reminders / Tasks integration
        return List.of();
    }
}

