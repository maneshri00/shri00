package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import com.productivity_mangement.productivity.service.UserGoalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/goals")
public class UserGoalController {

    private final UserGoalService userGoalService;

    public UserGoalController(UserGoalService userGoalService) {
        this.userGoalService = userGoalService;
    }


    @GetMapping
    public UserGoalProfile getGoals() {
        return userGoalService.getCurrentProfile();
    }


}


