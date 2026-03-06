package com.productivity_mangement.productivity.DTO;

import lombok.Data;

//package com.poductivity_mangement.productivity.dto;
@Data
public class CalendarEventDTO {

    private String id;
    private String title;
    private String start;
    private String end;
    private boolean allDay;
    private String organizer;


}
