package com.productivity_mangement.productivity.service;

//import com.google.api.services.calendar.model.Calendar;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.Calendar;

//import com.poductivity_mangement.productivity.DTO.*;
import com.productivity_mangement.productivity.DTO.*;
//import com.poductivity_mangement.productivity.controller.GoogleAuthController;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
//import java.util.Calendar;

@Service
public class CalendarService {

    private final GoogleOAuthService oauthService;

    public CalendarService(GoogleOAuthService oauthService) {
        this.oauthService = oauthService;
    }

    public List<CalendarEventDTO> getCalendarEvents() throws IOException {

        Credential cred = oauthService.getCredential();
        if (cred == null) {
            throw new IllegalStateException("Login first");
        }

        Calendar calendar = new Calendar.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                cred
        ).setApplicationName("Tusk").build();

        Events events = calendar.events()
                .list("primary")
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .setMaxResults(10)
                .execute();

        List<CalendarEventDTO> result = new ArrayList<>();

        for (Event e : events.getItems()) {
            result.add(toCalendarDTO(e));
        }

        return result;
    }
    private CalendarEventDTO toCalendarDTO(Event event) {

        CalendarEventDTO dto = new CalendarEventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getSummary());

        if (event.getOrganizer() != null) {
            dto.setOrganizer(event.getOrganizer().getEmail());
        }

        EventDateTime start = event.getStart();
        EventDateTime end = event.getEnd();

        if (start.getDateTime() != null) {
            dto.setStart(start.getDateTime().toString());
            dto.setEnd(end.getDateTime().toString());
            dto.setAllDay(false);
        } else {
            dto.setStart(start.getDate().toString());
            dto.setEnd(end.getDate().toString());
            dto.setAllDay(true);
        }

        return dto;
    }
    public CalendarContext getTodayContext() throws IOException {

        Credential cred = oauthService.getCredential();
        if (cred == null) throw new IllegalStateException("Login first");

        Calendar calendar = new Calendar.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                cred
        ).setApplicationName("Tusk").build();


        ZoneId zone = ZoneId.systemDefault();

        ZonedDateTime startOfDay = LocalDate.now(zone)
                .atStartOfDay(zone);

        ZonedDateTime endOfDay = LocalDate.now(zone)
                .atTime(23, 59, 59)
                .atZone(zone);

        DateTime start = new DateTime(startOfDay.toInstant().toEpochMilli());
        DateTime end = new DateTime(endOfDay.toInstant().toEpochMilli());

        Events events = calendar.events()
                .list("primary")
                .setTimeMin(start)
                .setTimeMax(end)
                .setSingleEvents(true)
                .execute();

        CalendarContext ctx = new CalendarContext();
        ctx.setTotalEventsToday(events.getItems().size());

        List<String> keywords = new ArrayList<>();
        boolean meetingSoon = false;

        for (Event e : events.getItems()) {

            if (e.getSummary() != null) {
                keywords.add(e.getSummary().toLowerCase());
            }

            if (e.getStart().getDateTime() != null) {

                Instant eventInstant =
                        Instant.ofEpochMilli(e.getStart().getDateTime().getValue());

                long minutes =
                        Duration.between(Instant.now(), eventInstant).toMinutes();

                if (minutes >= 0 && minutes <= 120) {
                    meetingSoon = true;
                }
            }
        }

        ctx.setHasMeetingSoon(meetingSoon);
        ctx.setEventKeywords(keywords);

        System.out.println("Calendar events today: " + ctx.getTotalEventsToday());
        System.out.println("Meeting soon: " + ctx.isHasMeetingSoon());
        System.out.println("Calendar keywords: " + ctx.getEventKeywords());

        return ctx;
    }
    public Task toFixedTask(CalendarEventDTO event) {

        Task task = new Task();

        task.setExternalId("CAL-" + event.getId());


        task.setTitle(event.getTitle());
        task.setSource(TaskSource.CALENDAR);
        task.setStatus(TaskStatus.TODO);

        Instant startInstant = Instant.parse(event.getStart());
        Instant endInstant = Instant.parse(event.getEnd());

        task.setFixedStartTime(
                LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())
        );

        task.setFixedEndTime(
                LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault())
        );

        task.setEstimatedMinutes(
                (int) Duration.between(
                        task.getFixedStartTime(),
                        task.getFixedEndTime()
                ).toMinutes()
        );

        task.getReasons().add("Fixed calendar event");

        return task;
    }
    public List<Task> getFixedTasksFromCalendar() throws IOException {

        List<CalendarEventDTO> events = getCalendarEvents();
        List<Task> tasks = new ArrayList<>();

        for (CalendarEventDTO e : events) {
            if (!e.isAllDay()) {
                tasks.add(toFixedTask(e));
            }
        }
        return tasks;
    }



}
