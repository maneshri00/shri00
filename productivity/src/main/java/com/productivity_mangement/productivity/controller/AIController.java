package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.RawMessageRequest;
import com.productivity_mangement.productivity.DTO.StructuredTaskDTO;
import com.productivity_mangement.productivity.service.AIProcessingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIProcessingService aiProcessingService;

    public AIController(AIProcessingService aiProcessingService) {
        this.aiProcessingService = aiProcessingService;
    }

    @PostMapping("/extract")
    public StructuredTaskDTO extract(@RequestBody RawMessageRequest req) {
        return aiProcessingService.extractStructured(
                req.getTaskTitle(),
                req.getContent(),
                req.getSender(),
                req.getSourcePlatform()
        );
    }
}

