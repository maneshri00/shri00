package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.*;
import com.productivity_mangement.productivity.entity.UserProfile;
import com.productivity_mangement.productivity.repository.TaskRepository;
import com.productivity_mangement.productivity.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import com.productivity_mangement.productivity.repository.UserProfileRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;
    private final TaskRepository taskRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(
            UserProfileRepository repository,
            TaskRepository taskRepository,
            UserProfileRepository userProfileRepository
    ) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public void saveProfile(String email, UserProfileRequest request) {

        UserProfile profile = new UserProfile();

        profile.setEmail(email);
        profile.setName(request.getName());
        profile.setProfession(request.getProfession());
        profile.setGoal(request.getGoal());
        profile.setWakeTime(request.getWakeTime());
        profile.setSleepTime(request.getSleepTime());
        profile.setPriorities(request.getPriorities());

        // String -> LocalDate
        // store as String
        profile.setDob(request.getDob());

        if (request.getDob() != null && !request.getDob().isEmpty()) {

            LocalDate dob = LocalDate.parse(request.getDob());
            int age = Period.between(dob, LocalDate.now()).getYears();
            profile.setAge(age);
        }




        repository.save(profile);
    }


    public UserProfileRequest getProfile(String email) {

        UserProfile p =
                repository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Profile not found"));

        UserProfileRequest dto = new UserProfileRequest();

        dto.setName(p.getName());
        dto.setProfession(p.getProfession());
        dto.setGoal(p.getGoal());

        // LocalDate -> String
        dto.setDob(
                p.getDob() != null ? p.getDob().toString() : null
        );

        dto.setWakeTime(p.getWakeTime());
        dto.setSleepTime(p.getSleepTime());
        dto.setPriorities(p.getPriorities());

        return dto;
    }


    public UserGoalProfile buildGoalProfile(String email) {

        return repository.findByEmail(email)
                .map(p -> {

                    UserGoalProfile g = new UserGoalProfile();

                    g.setLongTermGoal(p.getGoal());
                    g.setFocusAreas(p.getPriorities());

                    g.setUrgencySensitivity(3);
                    g.setCareerWeight(3);
                    g.setHealthWeight(3);

                    return g;
                })
                .orElseGet(() -> {

                    UserGoalProfile g = new UserGoalProfile();

                    g.setLongTermGoal("General Productivity");
                    g.setFocusAreas(List.of());

                    g.setUrgencySensitivity(3);
                    g.setCareerWeight(3);
                    g.setHealthWeight(3);

                    return g;
                });
    }

    public void updatePriorities(String email, List<String> priorities) {

        UserProfile p =
                repository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Profile not found"));

        p.setPriorities(priorities);
        repository.save(p);
    }

    public UserProfileView getProfileView(String email) {

        UserProfile p =
                repository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Profile not found"));

        long completed =
                taskRepository.countByUserEmailAndStatus(
                        email,
                        TaskStatus.DONE
                );

        long total =
                taskRepository.countByUserEmail(email);

        UserProfileView view = new UserProfileView();

        view.setName(p.getName());
        view.setAge(p.getAge());

        // LocalDate -> String
        view.setDob(
                p.getDob() != null ? p.getDob().toString() : null
        );

        view.setWakeTime(p.getWakeTime());
        view.setSleepTime(p.getSleepTime());
        view.setCompletedTaskCount(completed);
        view.setTotalTaskCount(total);

        return view;
    }

    public long getTotalTasks(String email) {
        return taskRepository.countByUserEmail(email);
    }

    public long getCompletedTasks(String email) {
        return taskRepository.countByUserEmailAndStatus(
                email,
                TaskStatus.DONE
        );
    }
    public void updateGoal(String email, String goal) {
        UserProfile profile =
                userProfileRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Profile not found"));

        profile.setGoal(goal);
        userProfileRepository.save(profile);
    }



}
