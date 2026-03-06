package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UserGoalService {

    private final UserProfileService profileService;
    private final HttpServletRequest request;

    public UserGoalService(UserProfileService profileService,
                           HttpServletRequest request) {
        this.profileService = profileService;
        this.request = request;
    }

    public UserGoalProfile getCurrentProfile() {

        String email = (String) request.getSession().getAttribute("email");

        if (email == null) {
            throw new IllegalStateException("User not logged in");
        }

        return profileService.buildGoalProfile(email);
    }
    public void updateGoal(UserGoalProfile profile) {
    }


}
