package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TimeSlot {

    private LocalDateTime start;
    private LocalDateTime end;
    private Task task;

}
