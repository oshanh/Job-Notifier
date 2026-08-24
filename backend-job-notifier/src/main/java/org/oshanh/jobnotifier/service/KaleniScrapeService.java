package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.oshanh.jobnotifier.model.KaleniUniJob;
import org.oshanh.jobnotifier.repository.KaleniUniJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KaleniScrapeService {

        private final KaleniUniJobRepository kaleniUniJobRepository;
        private final EmailProducer emailProducer;

        private static final String VACANCIES_URL = "https://www.kln.ac.lk/vacancies";
        private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy"); // e.g. Sep
                                                                                                             // 05, 2024

        @Value("${notify.email}")
        private String notifyEmail;

        // @Transactional
        @Scheduled(fixedRate = 120, timeUnit = java.util.concurrent.TimeUnit.MINUTES) // Run every 2 hours
        public List<KaleniUniJob> scrapeKaleniyaJobs() {
                log.info("Scraping Kaleniya University Jobs");
                List<KaleniUniJob> scrapedJobs = new ArrayList<>();

                try {
                        Document doc = Jsoup.connect(VACANCIES_URL)
                                        .userAgent(USER_AGENT)
                                        .timeout(15_000)
                                        .get();

                        // Find the embedded JS Array in data-code
                        // Google Sites embed widgets have a div where data-code contains the HTML/JS
                        // payload
                        String rawCode = "";
                        for (Element el : doc.select("div[data-code]")) {
                                rawCode = Parser.unescapeEntities(el.attr("data-code"), true);
                                if (rawCode.contains("salary:") && rawCode.contains("title:")) {
                                        break;
                                }
                        }

                        if (rawCode.isBlank()) {
                                log.warn("Could not find any embedded JS array container. Google Sites structure might have changed.");
                                return new ArrayList<>();
                        }

                        // Pluck out loosely formatted JSON objects containing standard keys
                        Pattern objPattern = Pattern.compile(
                                        "priority\\s*:\\s*\\d+.*?title\\s*:(.*?)(?=priority\\s*:|\\Z)", Pattern.DOTALL);
                        Matcher objMatcher = objPattern.matcher(rawCode);

                        while (objMatcher.find()) {
                                String objStr = objMatcher.group();

                                KaleniUniJob job = new KaleniUniJob();
                                job.setSource("University of Kelaniya");
                                job.setCompany("University of Kelaniya");
                                job.setExternalUrl(VACANCIES_URL);

                                job.setTitle(extractField(objStr, "title"));
                                job.setEmploymentType(extractField(objStr, "kind"));

                                String ribbon = extractField(objStr, "ribbon");
                                if (ribbon != null) {
                                        job.setDepartment(ribbon.replace("Vacancy •", "")
                                                        .replace("Vacancy -", "").trim());
                                }

                                job.setDescription(extractField(objStr, "summary"));
                                job.setSalary(extractField(objStr, "salary"));

                                String externalId = extractField(objStr, "_id");
                                if (externalId == null || externalId.isBlank()) {
                                        externalId = generateSlug(job.getTitle() + "-" + job.getDepartment());
                                }
                                job.setExternalId(externalId);
                                job.setPortalUrl(VACANCIES_URL + "#" + externalId);

                                String adUrl = extractUrlFromLinks(objStr, "Advertisement");
                                if (adUrl != null) {
                                        job.setAdvertisementUrl(adUrl);
                                }

                                String appUrl = extractUrlFromLinks(objStr, "Application|Apply|Form");
                                if (appUrl != null) {
                                        job.setApplicationUrl(appUrl);
                                }

                                String deadlineStr = extractField(objStr, "closing");
                                if (deadlineStr != null && !deadlineStr.isBlank()
                                                && !deadlineStr.equalsIgnoreCase("no deadline")) {
                                        try {
                                                job.setDeadline(LocalDate.parse(deadlineStr.trim(),
                                                                DATE_FORMATTER));
                                        } catch (DateTimeParseException ignored) {
                                        }
                                }
                                scrapedJobs.add(job);
                        }

                } catch (IOException e) {
                        log.error("Network error while scraping Kaleniya Uni: {}", e.getMessage());
                }

                // Deduplicate and save logic
                if (scrapedJobs.isEmpty())
                        return new ArrayList<>();

                Set<String> existingIds = kaleniUniJobRepository.findAll().stream()
                                .map(KaleniUniJob::getExternalId)
                                .collect(Collectors.toSet());

                List<KaleniUniJob> newJobs = scrapedJobs.stream()
                                .filter(j -> j.getExternalId() != null && existingIds.add(j.getExternalId()))
                                .toList();

                if (!newJobs.isEmpty()) {
                        kaleniUniJobRepository.saveAll(newJobs);
                        log.info("Saved {} new Kaleniya jobs.", newJobs.size());

                        for (KaleniUniJob newJob : newJobs) {
                                try {
                                        emailProducer.sendKaleniEmail(
                                                        new org.oshanh.jobnotifier.dto.KaleniEmailMessage(
                                                                        newJob.getTitle(),
                                                                        newJob.getDeadline(),
                                                                        newJob.getPortalUrl(),
                                                                        notifyEmail,
                                                                        newJob.getDepartment(),
                                                                        newJob.getEmploymentType(),
                                                                        newJob.getSalary(),
                                                                        newJob.getDescription(),
                                                                        newJob.getAdvertisementUrl(),
                                                                        newJob.getApplicationUrl()));
                                } catch (Exception e) {
                                        log.error("Failed to send Kaleni Uni email for job {}", newJob.getExternalId(),
                                                        e);
                                }
                        }
                }

                return newJobs;
        }

        private String extractField(String objStr, String fieldName) {
                // Looks for fieldName: "Value" OR fieldName: 'Value'
                Pattern p = Pattern.compile(fieldName + "\\s*:\\s*[\"'](.*?)[\"']", Pattern.DOTALL);
                Matcher m = p.matcher(objStr);
                if (m.find()) {
                        return m.group(1).trim();
                }
                return null;
        }

        private String extractUrlFromLinks(String objStr, String keywordPattern) {
                Pattern p = Pattern.compile("links\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
                Matcher m = p.matcher(objStr);
                if (m.find()) {
                        String linksSection = m.group(1);
                        // Try to find specific label match
                        Pattern specificPattern = Pattern.compile(
                                        "label\\s*:\\s*[\"'][^\"]*?(?:" + keywordPattern
                                                        + ")[^\"]*?[\"']\\s*,\\s*url\\s*:\\s*[\"'](.*?)[\"']",
                                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                        Matcher specificMatcher = specificPattern.matcher(linksSection);
                        if (specificMatcher.find()) {
                                return specificMatcher.group(1).trim();
                        }

                        // If no specific match was requested or found and keyword was empty/fallback
                        if (keywordPattern.isBlank()) {
                                Pattern anyPattern = Pattern.compile("url\\s*:\\s*[\"'](.*?)[\"']", Pattern.DOTALL);
                                Matcher anyMatcher = anyPattern.matcher(linksSection);
                                if (anyMatcher.find()) {
                                        return anyMatcher.group(1).trim();
                                }
                        }
                }
                return null;
        }

        private String generateSlug(String text) {
                if (text == null)
                        return "";
                return text.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }
}
