package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.mapper.JobMapper;
import org.oshanh.jobnotifier.model.UniRuhunaJob;
import org.oshanh.jobnotifier.model.Website;
import org.oshanh.jobnotifier.repository.UniRuhunaJobRepository;
import org.oshanh.jobnotifier.repository.WebsiteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniRuhunaScrapeService {

    private final UniRuhunaJobRepository uniRuhunaJobRepository;
    private final WebsiteRepository websiteRepository;
    private final PrefService prefService;

    private static final String TARGET_URL = "https://www.ruh.ac.lk/index.php/en/component/sppagebuilder?view=page&id=55";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    @Transactional
    @Scheduled(fixedRate = 120, timeUnit = TimeUnit.MINUTES)
    public List<JobDTO> scrapeRuhunaJobs() {
        log.info("Scraping University of Ruhuna Jobs");

        Website website = websiteRepository.findByBaseURL("https://www.ruh.ac.lk");
        List<UniRuhunaJob> existingJobs = uniRuhunaJobRepository.findAll();
        List<UniRuhunaJob> scrapedJobs = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(TARGET_URL)
                    .userAgent(USER_AGENT)
                    .timeout(15_000)
                    .get();

            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String text = link.text().trim();
                String href = link.absUrl("href");

                if (text.isEmpty() || href.isEmpty()) {
                    continue;
                }

                String lowerText = text.toLowerCase();
                String lowerHref = href.toLowerCase();

                // Advanced filtering to catch job openings reliably without catching unrelated
                // UI elements
                if ((lowerText.contains("vacanc") || lowerText.contains("post of") ||
                        lowerText.contains("lecturer") || lowerText.contains("application") ||
                        lowerHref.endsWith(".pdf")) && text.length() > 5) {

                    UniRuhunaJob job = new UniRuhunaJob();
                    job.setPosition(text);
                    job.setJobUrl(href);

                    scrapedJobs.add(job);
                }
            }

        } catch (IOException e) {
            log.error("Network error while scraping University of Ruhuna: {}", e.getMessage());
        }

        if (scrapedJobs.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> existingSignatures = existingJobs.stream()
                .map(j -> j.getJobUrl() + "|" + j.getPosition())
                .collect(Collectors.toSet());

        Set<UniRuhunaJob> newJobs = new HashSet<>();
        for (UniRuhunaJob scraped : scrapedJobs) {
            String signature = scraped.getJobUrl() + "|" + scraped.getPosition();
            if (!existingSignatures.contains(signature)) {
                newJobs.add(scraped);
                existingSignatures.add(signature); // Avoid duplicates within same scrape
            }
        }

        if (newJobs.isEmpty()) {
            return new ArrayList<>();
        }

        List<UniRuhunaJob> savedJobs = uniRuhunaJobRepository.saveAll(newJobs);
        log.info("Saved {} new University of Ruhuna jobs.", savedJobs.size());

        List<JobDTO> savedJobDTOS = JobMapper.toUniRuhunaJobsToJob(new HashSet<>(savedJobs));

        if (!savedJobDTOS.isEmpty()) {
            try {
                prefService.sendEmailForPreference(savedJobDTOS, website);
            } catch (Exception e) {
                log.error("Error pushing emails for Ruhuna jobs: {}", e.getMessage());
            }
        }
        else {
            log.info("No new University of Ruhuna jobs found.");
        }

        return savedJobDTOS;
    }
}
