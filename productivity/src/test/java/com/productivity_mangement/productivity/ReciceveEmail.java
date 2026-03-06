package com.productivity_mangement.productivity;

//import com.poductivity_mangement.productivity.service.CalendarService;
//import com.poductivity_mangement.productivity.service.EmailService;
//import com.poductivity_mangement.productivity.service.GmailService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@SpringBootTest
//public class ReciceveEmail {
//    @Autowired
//    private EmailService emailService;
////    reciving email test
//    @Test
//    void getInbox(){
//        emailService.getInboxMessages();
//    }
//}
////
////@RestController
//@RequestMapping("/test")
//public class TestController {
//
//    private final GmailService gmailService;
//    private final CalendarService calendarService;
//
//    public TestController(GmailService gmailService, CalendarService calendarService) {
//        this.gmailService = gmailService;
//        this.calendarService = calendarService;
//    }
//
//    @GetMapping("/gmail")
//    public String gmail() throws IOException {
//        gmailService.readInbox();
//        return "Gmail Read Done";
//    }
//
//    @GetMapping("/calendar")
//    public String calendar() throws IOException {
//        calendarService.readEvents();
//        return "Calendar Read Done";
//    }
//}
