package com.productivity_mangement.productivity.DTO;

import lombok.Data;

@Data
public class RawMessageRequest {
    private String taskTitle;
    private String content;
    private String sender;
    private String sourcePlatform;
}

