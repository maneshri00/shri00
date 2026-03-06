package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CalendarContext {

    private int totalEventsToday;
    private boolean hasMeetingSoon;
    private List<String> eventKeywords;
}

