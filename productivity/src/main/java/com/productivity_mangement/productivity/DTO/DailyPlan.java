package com.productivity_mangement.productivity.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyPlan {

    private LocalDate date;
    private List<TimeSlot> slots = new ArrayList<>();

    public DailyPlan(LocalDate date) {
        this.date = date;
    }

    public void addSlot(TimeSlot slot) {
        slots.add(slot);
    }

    public LocalDate getDate() {
        return date;
    }


    public List<TimeSlot> getSlots() {
        return slots;
    }
}
