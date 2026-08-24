package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.dto.TesTGmailDTO;
import org.oshanh.jobnotifier.model.JobListing;
import org.oshanh.jobnotifier.service.NotificationService;
import org.oshanh.jobnotifier.service.ScrapeService;
import org.oshanh.jobnotifier.service.AIService;
import org.oshanh.jobnotifier.service.KaleniScrapeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "test")
@RequiredArgsConstructor
public class TestController {
    private final ScrapeService scrapeService;
    private final NotificationService notificationService;
    private final AIService AIService;
    private final KaleniScrapeService scraperService;

    @Value("${notify.email}")
    private String notifyEmail;

    @GetMapping(value = "scrape-topjobs")
    public List<JobDTO> testScrape() {
        return scrapeService.scrapeTopjobs();
    }

    @PostMapping(value = "gmail")
    public boolean testGmail(@RequestBody TesTGmailDTO tesTGmailDTO) {
        System.out.println(tesTGmailDTO.toString());
        return notificationService.sendTestGmailNotification(tesTGmailDTO.getEmail(), tesTGmailDTO.getSubject(),
                tesTGmailDTO.getMessage());
    }

    @GetMapping(value = "scrape-airport")
    public List<JobDTO> testAirportScrape() {
        return scrapeService.scrapeAirportJobs();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Tell me a joke about Java") String message) {
        return AIService.sendEmailWithAiMessage(notifyEmail, message);
    }

    @GetMapping("/scrape-kaleniya-uni")
    public ResponseEntity<?> getVacancies() {
        try {
            return ResponseEntity.ok(scraperService.scrapeKaleniyaJobs());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

}
