package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.DailyPlan;
import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.TimeSlot;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DailyPlanService {

    public DailyPlan generate(
            List<Task> tasks,
            LocalDateTime dayStart,
            LocalDateTime dayEnd
    ) {

        System.out.println("DailyPlanService received tasks: " + tasks.size());

        DailyPlan dailyPlan = new DailyPlan(dayStart.toLocalDate());

        // ---------- FIXED TASKS ----------
        List<Task> fixed = tasks.stream()
                .filter(t ->
                        t.isFixed()
                                && t.getFixedStartTime() != null
                                && t.getFixedEndTime() != null
                )
                .sorted(Comparator.comparing(Task::getFixedStartTime))
                .toList();

        // ---------- FLEXIBLE TASKS ----------
        List<Task> flexible = tasks.stream()
                .filter(t -> !t.isFixed())
                .sorted(Comparator.comparingInt(Task::getPriorityScore).reversed())
                .toList();

        System.out.println("Fixed: " + fixed.size());
        System.out.println("Flexible: " + flexible.size());

        LocalDateTime cursor = dayStart;
        int flexIndex = 0;

        // ---------- PLACE TASKS AROUND FIXED ----------
        for (Task f : fixed) {

            while (cursor.isBefore(f.getFixedStartTime())
                    && flexIndex < flexible.size()) {

                Task t = flexible.get(flexIndex);

                int minutes = (t.getEstimatedMinutes() != null
                        && t.getEstimatedMinutes() > 0)
                        ? t.getEstimatedMinutes()
                        : 60;   // default 60 minutes

                LocalDateTime end = cursor.plusMinutes(minutes);

                if (end.isAfter(f.getFixedStartTime())) break;

                dailyPlan.addSlot(createSlot(cursor, end, t));
                cursor = end;
                flexIndex++;
            }

            // add fixed task
            dailyPlan.addSlot(
                    createSlot(
                            f.getFixedStartTime(),
                            f.getFixedEndTime(),
                            f
                    )
            );

            cursor = f.getFixedEndTime();
        }

        // ---------- FILL REST OF DAY ----------
        while (cursor.isBefore(dayEnd)
                && flexIndex < flexible.size()) {

            Task t = flexible.get(flexIndex);

            int minutes = (t.getEstimatedMinutes() != null
                    && t.getEstimatedMinutes() > 0)
                    ? t.getEstimatedMinutes()
                    : 60;

            LocalDateTime end = cursor.plusMinutes(minutes);

            if (end.isAfter(dayEnd)) break;

            dailyPlan.addSlot(createSlot(cursor, end, t));
            cursor = end;
            flexIndex++;
        }

        System.out.println("Generated slots: " + dailyPlan.getSlots().size());

        return dailyPlan;
    }

    // ---------- SLOT BUILDER ----------
    private TimeSlot createSlot(
            LocalDateTime start,
            LocalDateTime end,
            Task task
    ) {
        TimeSlot ts = new TimeSlot();
        ts.setStart(start);
        ts.setEnd(end);
        ts.setTask(task);
        return ts;
    }
}
