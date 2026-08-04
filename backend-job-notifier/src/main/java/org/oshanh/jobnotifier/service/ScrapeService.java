package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oshanh.jobnotifier.dto.Job;
import org.oshanh.jobnotifier.mapper.JobMapper;
import org.oshanh.jobnotifier.model.Topjobs;
import org.oshanh.jobnotifier.model.Website;
import org.oshanh.jobnotifier.model.WebsiteURL;
import org.oshanh.jobnotifier.repository.TopjobsRepository;
import org.oshanh.jobnotifier.repository.WebsiteRepository;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@AllArgsConstructor
public class ScrapeService {

    private final TopjobsRepository topjobsRepository;
    private final WebsiteRepository websiteRepository;
    private final NotificationService notificationService;
    private final PrefService prefService;

    public List<Topjobs> topjobs() {

        Website website = websiteRepository.findByBaseURL("www.topjobs.lk");
        List<String> URLs = website.getUrls().stream().map(WebsiteURL::getUrl).toList();

        List<Topjobs> jobs = new ArrayList<>();

        for (String u : URLs) {
            try {
                Document doc = Jsoup.connect(u).get();
                Elements rows = doc.select("table tbody tr[id^=tr]");

                for (Element row : rows) {
                    Elements cells = row.select("td");

                    String jobCode = row.selectFirst("span[id^=hdnJC]").text();
                    String empCode = row.selectFirst("span[id^=hdnEC]").text();
                    String agentCode = row.selectFirst("span[id^=hdnAC]").text();
                    String rid = row.attr("id").replaceAll("[^0-9]", "");

                    int refNo = Integer.parseInt(cells.get(1).text());
                    String position = cells.get(2).select("h2 span").text();
                    String companyName = cells.get(2).select("h1").text();
                    LocalDate openingDate = convertTopJobsDate(cells.get(4).text());
                    LocalDate closingDate = convertTopJobsDate(cells.get(5).text());
                    String location = cells.get(6).text();
                    String url = buildTopJobUrl(rid, agentCode, jobCode, empCode);

                    Topjobs job = Topjobs.builder()
                            .refNo(refNo)
                            .position(position)
                            .companyName(companyName)
                            .openingDate(openingDate)
                            .closingDate(closingDate)
                            .location(location)
                            .jobUrl(url)
                            .build();

                    jobs.add(job);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Set<Integer> existingJobs = topjobsRepository.getAllJobsRefNos();
        List<Topjobs> newJobs = jobs.stream().filter(job -> !existingJobs.contains(job.getRefNo())).toList();
        if (!newJobs.isEmpty()) {
            List<Topjobs> savedNewTopJobs = topjobsRepository.saveAll(newJobs);
            List<Job> savedJobs = JobMapper.topJobsToJob(savedNewTopJobs);
            try {

                return newJobs;
            } catch (Exception ignored) {

            } finally {
                prefService.sendEmailForPreference(savedJobs);
            }

        }

        return new ArrayList<>();

    }

    // convert date strings to LocalDate
    public LocalDate convertTopJobsDate(String raw) {
        if (raw == null || raw.isBlank())
            return null;
        try {
            return LocalDate.parse(raw.trim(),
                    DateTimeFormatter.ofPattern("EEE MMM d yyyy", Locale.ENGLISH));
        } catch (DateTimeParseException e) {

            return null;
        }
    }

    // TobJobs job url builder
    private static final String BASE_URL = "https://www.topjobs.lk/employer/JobAdvertismentServlet";
    private static final String PG_PARAM = "applicant/vacancybyfunctionalarea.jsp";

    private String buildTopJobUrl(String rid, String agentCode, String jobCode, String empCode) {

        return UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("rid", rid)
                .queryParam("ac", agentCode)
                .queryParam("jc", jobCode)
                .queryParam("ec", empCode)
                .queryParam("pg", PG_PARAM)
                .toUriString();
    }

    //airport jobs
    //@Scheduled(cron = "0 0 0 * * *",zone ="Asia/Colombo")
    @Scheduled(fixedRate = 60 * 60 * 1000) // 5 minutes in ms
    public List<Job> scrapeAirportJobs() {
        Website website = websiteRepository.findByBaseURL("www.airport.lk");
        List<String> URLs = new ArrayList<>();
        if (website != null) {
            URLs = website.getUrls().stream().map(WebsiteURL::getUrl).toList();
        } else {
            // Fallback for testing if database is not set up
            URLs.add("https://www.airport.lk/aasl/careers/careers");
        }

        List<Job> jobs = new ArrayList<>();

        for (String u : URLs) {
            try {
                Document doc = Jsoup.connect(u).get();
                Elements rows = doc.select("table.table tbody tr");

                for (Element row : rows) {
                    Elements cells = row.select("td");
                    // Ensure the row has enough columns (some might be completely empty or headers)
                    if (cells.size() >= 4) {
                        String position = cells.get(1).text().trim();
                        String closingDateStr = cells.get(2).text().trim();

                        Element linkElement = cells.get(3).selectFirst("a");
                        String jobUrl = u; // fallback to the page URL
                        if (linkElement != null) {
                            String href = linkElement.absUrl("href");
                            if (href != null && !href.isEmpty()) {
                                jobUrl = href;
                            }
                        }

                        Job job = new Job();
                        job.setPosition(position);
                        job.setCompanyName("Airport and Aviation Services");
                        job.setSource(jobUrl);

                        if (!closingDateStr.equalsIgnoreCase("N/A") && !closingDateStr.isEmpty()) {
                            try {
                                job.setClosingDate(LocalDate.parse(closingDateStr));
                            } catch (DateTimeParseException ignored) {
                            }
                        }

                        jobs.add(job);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        notificationService.sendNewJobPostingsNotification("oshanharshad3@gmail.com",jobs);
        notificationService.sendNewJobPostingsNotification("oshanedu@gmail.com",jobs);
        return jobs;
    }
}
