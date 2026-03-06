package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.NotionTaskDTO;
import com.productivity_mangement.productivity.service.NotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @GetMapping("/notion/tasks")
    public List<NotionTaskDTO> tasks() {
        return notionService.getTasks();
    }
}

