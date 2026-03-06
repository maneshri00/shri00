package com.productivity_mangement.productivity.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/priority")
public class PriorityOverrideController {

    private final Map<String, Integer> overrides = new HashMap<>();

    @PostMapping("/{taskId}")
    public void overridePriority(
            @PathVariable String taskId,
            @RequestParam int boost
    ) {
        overrides.put(taskId, boost);
    }

    public Integer getOverride(String taskId) {
        return overrides.get(taskId);
    }
}
