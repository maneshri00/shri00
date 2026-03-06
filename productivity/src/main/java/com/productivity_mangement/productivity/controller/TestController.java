package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.service.CalendarService;
import com.productivity_mangement.productivity.service.GmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {

    private final GmailService gmailService;
    private final CalendarService calendarService;

    public TestController(GmailService gmailService,
                          CalendarService calendarService) {
        this.gmailService = gmailService;
        this.calendarService = calendarService;
    }

    @GetMapping("/gmail")
    public String gmail() throws IOException {
        gmailService.readInboxAsTasks();
        return "Gmail Read Done";
    }

    @GetMapping("/calendar")
    public String calendar() throws IOException {
        calendarService.getCalendarEvents();
        return "Calendar Read Done";
    }
}
