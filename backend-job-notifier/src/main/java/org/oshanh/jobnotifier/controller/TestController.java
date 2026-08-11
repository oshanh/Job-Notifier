package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.dto.TesTGmailDTO;
import org.oshanh.jobnotifier.service.NotificationService;
import org.oshanh.jobnotifier.service.ScrapeService;
import org.oshanh.jobnotifier.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "test")
@RequiredArgsConstructor
public class TestController {
    private final ScrapeService scrapeService;
    private final NotificationService notificationService;
    private final AIService AIService;

    @Value("${notify.email}")
    private String notifyEmail;

    @GetMapping(value = "scrape-topjobs")
    public List<JobDTO> testScrape(){
        return scrapeService.scrapeTopjobs();
    }

    @PostMapping(value = "gmail")
    public boolean testGmail (@RequestBody TesTGmailDTO tesTGmailDTO){
        System.out.println(tesTGmailDTO.toString());
        return notificationService.sendTestGmailNotification(tesTGmailDTO.getEmail(), tesTGmailDTO.getSubject(), tesTGmailDTO.getMessage());
    }

    @GetMapping(value = "scrape-airport")
    public List<JobDTO> testAirportScrape() {
        return scrapeService.scrapeAirportJobs();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Tell me a joke about Java") String message) {
        return AIService.sendEmailWithAiMessage(notifyEmail,message);
    }

}
