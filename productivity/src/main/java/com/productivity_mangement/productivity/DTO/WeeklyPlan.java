package com.productivity_mangement.productivity.DTO;

import java.util.ArrayList;
import java.util.List;

public class WeeklyPlan {

    private List<DailyPlan> days = new ArrayList<>();

    public void addDailyPlan(DailyPlan plan) {
        days.add(plan);
    }

    public List<DailyPlan> getDays() {
        return days;
    }
}

