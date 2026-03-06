package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.CalendarContext;
import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.TaskSource;
import com.productivity_mangement.productivity.DTO.UserGoalProfile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class PriorityEngine {


        public void score(Task task,
                UserGoalProfile user,
                CalendarContext calendar) {

            if (user == null) {
                task.setPriorityScore(0);
                task.getReasons().add("User profile not set");
                return;
            }


        int score = 0;
        System.out.println("Task: " + task.getTitle());
        System.out.println("Final Score: " + score);
        System.out.println("Reasons: " + task.getReasons());
        System.out.println("------------");

        if (task.getDueDate() != null) {
            long hours = Duration.between(LocalDateTime.now(), task.getDueDate()).toHours();

            if (hours <= 24) {
                score += 50;
                task.getReasons().add("Due within 24 hours (+50)");
            } else if (hours <= 72) {
                score += 30;
                task.getReasons().add("Due within 3 days (+30)");
            }
        }


        if (task.getDueDate() != null && LocalDateTime.now().isAfter(task.getDueDate())) {
            score += 80;
            task.getReasons().add("Overdue (+80)");
        }
        if (task.getSource() == TaskSource.GMAIL) {
            score += 20;
            task.getReasons().add("Email task (+20)");
        } else if (task.getSource() == TaskSource.NOTION) {
            score += 10;
            task.getReasons().add("Planned Notion task (+10)");
        } else if (task.getSource() == TaskSource.MANUAL) {
            score += 15;
            task.getReasons().add("User-created task (+15)");
        }
        if (task.getSource() == TaskSource.GMAIL
                && isNoise(task.getTitle(), task.getContent())) {

            score -= 30;
            task.getReasons().add("Low-signal email (-30)");
        }
        if (matchesGoal(task, user)) {
            int goalScore = 40 * user.getCareerWeight();
            score += goalScore;
            task.getReasons().add(
                    "Aligns with long-term goal (+" + goalScore + ")"
            );
        }

        if (calendar != null) {

            if (calendar.getTotalEventsToday() >= 5) {
                score -= 20;
                task.getReasons().add("Meeting-heavy day (-20)");
            }

            if (calendar.isHasMeetingSoon()
                    && matchesCalendar(task, calendar)) {

                score += 30;
                task.getReasons().add("Relevant meeting coming up (+30)");
            }
        }


        score = score * user.getUrgencySensitivity();
        task.getReasons().add(
                "Urgency sensitivity x" + user.getUrgencySensitivity()
        );

        Integer override = task.getManualPriorityBoost();
        if (override != null) {
            score += override;
            task.getReasons().add("Manual priority override (" + override + ")");
        }

        task.setPriorityScore(score);
    }
    private boolean isNoise(String title, String content) {
        String t = ((title == null ? "" : title) + " " +
                (content == null ? "" : content)).toLowerCase();

        return t.contains("unsubscribe")
                || t.contains("newsletter")
                || t.contains("gift")
                || t.contains("offer")
                || t.contains("sale")
                || t.contains("promotion");
    }

    private boolean matchesGoal(Task task, UserGoalProfile user) {

        if (user == null || user.getFocusAreas() == null) {
            return false;
        }

        String title = safe(task.getTitle());
        String content = safe(task.getContent());

        for (String area : user.getFocusAreas()) {
            String keyword = area.toLowerCase();

            if (title.contains(keyword) || content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }


    private boolean matchesCalendar(Task task, CalendarContext calendar) {

        String text = (safe(task.getTitle()) + " " + safe(task.getContent()));

        for (String keyword : calendar.getEventKeywords()) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
