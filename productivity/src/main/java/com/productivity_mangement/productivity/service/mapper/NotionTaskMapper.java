package com.productivity_mangement.productivity.service.mapper;



import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.TaskSource;
import com.productivity_mangement.productivity.DTO.TaskStatus;
import com.productivity_mangement.productivity.DTO.NotionTaskDTO;
import com.productivity_mangement.productivity.service.NotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotionTaskMapper {

    private final NotionService notionService;

    public NotionTaskMapper(NotionService notionService) {
        this.notionService = notionService;
    }

    public List<Task> fetchNotionTasksAsTasks() {

        List<NotionTaskDTO> notionTasks = notionService.getTasks();
        List<Task> tasks = new ArrayList<>();

        for (NotionTaskDTO n : notionTasks) {

            Task task = new Task();
            task.setId(null);

            task.setSource(TaskSource.NOTION);
            task.setTitle(n.getTitle());
            task.setContent(n.getContent());
            task.setStatus(TaskStatus.TODO);
            task.setCreatedAt(LocalDateTime.now());

            task.setExternalId("NOTION-" + n.getId());

        }

        return tasks;
    }
}

