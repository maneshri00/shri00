package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTaskRequest {

    private String title;
    private String content;
    private LocalDateTime dueDate;
}

