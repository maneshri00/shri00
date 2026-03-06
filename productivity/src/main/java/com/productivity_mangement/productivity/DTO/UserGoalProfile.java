package com.productivity_mangement.productivity.DTO;

import lombok.Data;
import java.util.List;

@Data
public class UserGoalProfile {

    private String longTermGoal;
    private List<String> focusAreas;

    private int urgencySensitivity;
    private int careerWeight;
    private int healthWeight;
}
