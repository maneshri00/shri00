package com.productivity_mangement.productivity.DTO;

import lombok.Data;

@Data
public class PriorityScoreRequest {
    private Integer manual;    // 0-100
    private Integer behavior;  // 0-100
    private Integer context;   // 0-100
    private Integer aiUrgency; // 0-100
}

