package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.DTO.ProfileStats;
import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import com.productivity_mangement.productivity.DTO.UserProfileRequest;
import com.productivity_mangement.productivity.DTO.UserProfileView;
import com.productivity_mangement.productivity.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public String createProfile(
            @RequestBody UserProfileRequest request,
            HttpServletRequest httpRequest
    ) {
        String email =
                (String) httpRequest.getSession().getAttribute("email");

        service.saveProfile(email, request);
        return "Profile created";
    }

    @GetMapping("/me")
    public UserProfileRequest getMyProfile(HttpServletRequest request) {

        String email =
                (String) request.getSession().getAttribute("email");

        return service.getProfile(email);
    }


    @GetMapping("/goal")
    public UserGoalProfile getGoalProfile(HttpServletRequest request) {

        String email =
                (String) request.getSession().getAttribute("email");

        return service.buildGoalProfile(email);
    }

    @PostMapping("/update-priorities")
    public String updatePriorities(
            @RequestBody Map<String, List<String>> body,
            HttpServletRequest request
    ) {
        String email =
                (String) request.getSession().getAttribute("email");

        service.updatePriorities(email, body.get("priorities"));
        return "Priorities updated";
    }
    @GetMapping("/profile-view")
    public UserProfileView profileView(HttpServletRequest request) {

        String email =
                (String) request.getSession().getAttribute("email");

        return service.getProfileView(email);
    }
    @GetMapping("/stats")
    public ProfileStats stats(HttpServletRequest request) {

        String email =
                (String) request.getSession().getAttribute("email");

        ProfileStats s = new ProfileStats();
        s.setTotalTaskCount(service.getTotalTasks(email));
        s.setCompletedTaskCount(service.getCompletedTasks(email));

        return s;
    }

    @PostMapping("/goal")
    public String updateGoal(
            @RequestBody Map<String,String> body,
            HttpServletRequest request
    ) {
        String email =
                (String) request.getSession().getAttribute("email");

        service.updateGoal(email, body.get("goal"));
        return "Goal updated";
    }


}
