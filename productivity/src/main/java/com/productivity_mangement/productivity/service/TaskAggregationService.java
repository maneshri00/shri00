package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.*;
import com.productivity_mangement.productivity.entity.TaskEntity;
import com.productivity_mangement.productivity.repository.TaskRepository;
import com.productivity_mangement.productivity.service.mapper.NotionTaskMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskAggregationService {

    private final GmailService gmailService;
    private final NotionTaskMapper notionTaskMapper;
    private final CalendarService calendarService;
    private final PriorityEngine priorityEngine;
    private final TaskRepository taskRepository;

    public TaskAggregationService(
            GmailService gmailService,
            NotionTaskMapper notionTaskMapper,
            CalendarService calendarService,
            PriorityEngine priorityEngine,
            TaskRepository taskRepository
    ) {
        this.gmailService = gmailService;
        this.notionTaskMapper = notionTaskMapper;
        this.calendarService = calendarService;
        this.priorityEngine = priorityEngine;
        this.taskRepository = taskRepository;
    }

    // ---------- PRIORITY TASKS ----------

    public List<Task> getPrioritizedTasks(
            UserGoalProfile user,
            String email
    ) throws IOException {

        List<Task> allTasks = new ArrayList<>();

        taskRepository.findByUserEmail(email)
                .stream()
                .map(this::toDto)
                .forEach(allTasks::add);

        allTasks.addAll(gmailService.readInboxAsTasks());
        allTasks.addAll(notionTaskMapper.fetchNotionTasksAsTasks());
        allTasks.addAll(calendarService.getFixedTasksFromCalendar());

        CalendarContext context =
                calendarService.getTodayContext();

        for (Task task : allTasks) {
            task.setUserEmail(email);
            persistIfNotExists(task);
            priorityEngine.score(task, user, context);
        }

        allTasks.sort(
                Comparator.comparingInt(Task::getPriorityScore)
                        .reversed()
        );

        return allTasks;
    }



    public Task saveTask(Task task) {

        TaskEntity entity = mapToEntity(task);
        entity.setStatus(TaskStatus.TODO);

        TaskEntity saved = taskRepository.save(entity);

        task.setId(saved.getId());   // send DB id back

        return task;
    }


    public void markTaskDone(Long id) {

        TaskEntity task =
                taskRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Task not found"));

        task.setStatus(TaskStatus.DONE);

        taskRepository.save(task);
    }


    public List<TaskEntity> getCompletedTasks(String email) {

        return taskRepository
                .findByUserEmailAndStatus(
                        email,
                        TaskStatus.DONE
                );
    }


    private void persistIfNotExists(Task task) {

        boolean exists =
                taskRepository.existsByTitleAndUserEmail(
                        task.getTitle(),
                        task.getUserEmail()
                );

        if (!exists) {
            task.setStatus(TaskStatus.PENDING);

            TaskEntity saved =
                    taskRepository.save(mapToEntity(task));

            task.setId(saved.getId());
        }
    }



    private Task toDto(TaskEntity e) {

        Task t = new Task();

        t.setId(e.getId());

        t.setTitle(e.getTitle());
        t.setDescription(e.getDescription());
        t.setPriority(e.getPriority());
        t.setEstimatedMinutes(e.getEstimatedMinutes());
        t.setFixed(e.isFixed());
        t.setStatus(e.getStatus());

        if (e.getFixedStartTime() != null)
            t.setFixedStartTime(
                    e.getFixedStartTime()
                            .atDate(java.time.LocalDate.now())
            );

        if (e.getFixedEndTime() != null)
            t.setFixedEndTime(
                    e.getFixedEndTime()
                            .atDate(java.time.LocalDate.now())
            );

        return t;
    }



    private TaskEntity mapToEntity(Task task) {

        TaskEntity e = new TaskEntity();

        e.setTitle(task.getTitle());
        e.setDescription(task.getDescription());
        e.setPriority(task.getPriority());
        e.setEstimatedMinutes(task.getEstimatedMinutes());
        e.setFixed(task.isFixed());

        e.setUserEmail(task.getUserEmail());

        e.setStatus(task.getStatus());

        if (task.getFixedStartTime() != null)
            e.setFixedStartTime(task.getFixedStartTime().toLocalTime());

        if (task.getFixedEndTime() != null)
            e.setFixedEndTime(task.getFixedEndTime().toLocalTime());

        return e;
    }



}
