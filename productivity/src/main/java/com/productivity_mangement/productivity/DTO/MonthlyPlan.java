package com.productivity_mangement.productivity.DTO;

import java.util.ArrayList;
import java.util.List;

public class MonthlyPlan {

    private List<Task> highImpactTasks = new ArrayList<>();

    public void addHighImpactTask(Task task) {
        highImpactTasks.add(task);
    }

    public List<Task> getHighImpactTasks() {
        return highImpactTasks;
    }
}

