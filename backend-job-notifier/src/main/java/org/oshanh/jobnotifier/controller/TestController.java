package org.oshanh.jobnotifier.controller;

import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.Job;
import org.oshanh.jobnotifier.model.Topjobs;
import org.oshanh.jobnotifier.service.NotificationService;
import org.oshanh.jobnotifier.service.ScrapeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "test")
@AllArgsConstructor
public class TestController {
    private final ScrapeService scrapeService;
    private final NotificationService notificationService;

    @GetMapping(value = "scrape-topjobs")
    public List<Topjobs> testScrape(){
        return scrapeService.topjobs();
    }

    @GetMapping(value = "test-gmail")
    public void testGmail (){
        notificationService.sendGmailNotification("oshanharshad3@gmail.com","Test Email","Test Body");
    }

    @GetMapping(value = "scrape-airport")
    public List<Job> testAirportScrape() {
        return scrapeService.scrapeAirportJobs();
    }

}
