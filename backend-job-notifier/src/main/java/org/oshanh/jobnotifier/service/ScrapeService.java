package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.mapper.JobMapper;
import org.oshanh.jobnotifier.model.*;
import org.oshanh.jobnotifier.repository.AirportjobsRepository;
import org.oshanh.jobnotifier.repository.FosmisNoticeRepository;
import org.oshanh.jobnotifier.repository.TopjobsRepository;
import org.oshanh.jobnotifier.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapeService {

    private final TopjobsRepository topjobsRepository;
    private final WebsiteRepository websiteRepository;
    private final AirportjobsRepository airportjobsRepository;
    private final PrefService prefService;
    private final FosmisNoticeRepository fosmisNoticeRepository;
    private final NotificationService notificationService;

    // TobJobs job url builder
    private static final String BASE_URL = "https://www.topjobs.lk/employer/JobAdvertismentServlet";
    private static final String PG_PARAM = "applicant/vacancybyfunctionalarea.jsp";

    // FOSMIS
    private static final String BASE = "https://paravi.ruh.ac.lk/fosmis/";
    private static final String LOGIN_URL = BASE + "login.php";
    private static final String NOTICES_URL = BASE + "forms/form_53_a.php";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36";
    private static final DateTimeFormatter FOSMIS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");
    private Connection cachedSession;

    @Value("${notify.email}")
    private String notifyEmail;

    @Value("${fosmis.username}")
    private String username;

    @Value("${fosmis.pwd}")
    private String pwd;



    public List<JobDTO> scrapeTopjobs() {

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
            List<JobDTO> savedJobDTOS = JobMapper.topJobsToJob(savedNewTopJobs);
            try {

                return savedJobDTOS;
            } catch (Exception ignored) {

            } finally {
                prefService.sendEmailForPreference(savedJobDTOS);
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

    private String buildTopJobUrl(String rid, String agentCode, String jobCode, String empCode) {

        return UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("rid", rid)
                .queryParam("ac", agentCode)
                .queryParam("jc", jobCode)
                .queryParam("ec", empCode)
                .queryParam("pg", PG_PARAM)
                .toUriString();
    }

    // airport jobs
    // @Scheduled(cron = "0 0 0 * * *",zone ="Asia/Colombo")
    // @Scheduled(fixedRate = 60 * 60 * 1000)
    public List<JobDTO> scrapeAirportJobs() {
        Website website = websiteRepository.findByBaseURL("www.airport.lk");
        List<String> URLs = new ArrayList<>();
        if (website != null) {
            URLs = website.getUrls().stream().map(WebsiteURL::getUrl).toList();
        } else {
            // Fallback for testing if database is not set up
            URLs.add("https://www.airport.lk/aasl/careers/careers");
        }

        List<JobDTO> jobDTOS = new ArrayList<>();
        List<Airportjobs> airportjobs = airportjobsRepository.findAll();
        List<Airportjobs> scrapedAirportjobs = new ArrayList<>();

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
                        // save
                        Airportjobs ajob = new Airportjobs();
                        ajob.setJobUrl(jobUrl);
                        ajob.setPosition(position);

                        JobDTO jobDTO = new JobDTO();
                        if (!closingDateStr.equalsIgnoreCase("N/A") && !closingDateStr.isEmpty()) {
                            try {
                                jobDTO.setClosingDate(LocalDate.parse(closingDateStr));
                                ajob.setClosingDate(LocalDate.parse(closingDateStr));
                            } catch (DateTimeParseException ignored) {
                            }
                        }

                        jobDTO.setPosition(position);
                        jobDTO.setCompanyName("Airport");
                        jobDTO.setSource(jobUrl);

                        jobDTOS.add(jobDTO);
                        scrapedAirportjobs.add(ajob);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        // filter new jobs
        log.info(jobDTOS.toString());
        notificationService.sendNewJobPostingsNotification(notifyEmail,jobDTOS);
        return jobDTOS;
    }

    // FOSMIS

    public List<FosmisNotice> parse(Document noticesPage) {
        Elements rows = noticesPage.select("table tr.trbgc");
        List<FosmisNotice> notices = new ArrayList<>();

        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() < 4)
                continue;

            String dateText = cells.get(1).text().trim();
            String title = cells.get(2).text().trim();
            Element linkEl = cells.get(3).selectFirst("a[href]");
            if (linkEl == null)
                continue;
            String absoluteLink = encodeUrl(linkEl.absUrl("href")); // resolves ../ and relative paths
            // String absoluteLink =linkEl.absUrl("href"); // resolves ../ and relative
            // paths

            FosmisNotice notice = new FosmisNotice();
            notice.setTitle(title);
            notice.setLink(absoluteLink);
            try {
                notice.setPublishedAt(LocalDateTime.parse(dateText, FOSMIS_DATE_FORMAT));
            } catch (DateTimeParseException e) {
                notice.setPublishedAt(LocalDateTime.now()); // fallback, don't fail the whole row
            }
            notices.add(notice);
        }
        return notices;
    }

    private static String encodeUrl(String rawUrl) {
        try {
            URL url = new URL(rawUrl); // lenient — doesn't choke on the space
            URI encoded = new URI(
                    url.getProtocol(),
                    url.getAuthority(),
                    url.getPath(),
                    url.getQuery(),
                    null);
            return encoded.toASCIIString(); // this step does the actual %20 encoding
        } catch (MalformedURLException | URISyntaxException e) {
            return rawUrl; // fall back to raw if something unexpected slips through
        }
    }

    @Scheduled(fixedRate = 20, timeUnit = TimeUnit.MINUTES)
    public void checkForNewNotices() throws IOException {
        Document page = fetchNoticesPageWithCachedSession();
        List<FosmisNotice> scraped = parse(page);
        log.info("Scraped {} Notices", scraped.size());

        // reverse so oldest new notice emails first, in publish order
        Collections.reverse(scraped);

        for (FosmisNotice notice : scraped) {
            if (!fosmisNoticeRepository.existsByLink(notice.getLink())) {
                fosmisNoticeRepository.save(notice);
                //notificationService.sendFOSMISNotice(notice,notifyEmail);
            }
        }
    }

    public Document fetchNoticesPageWithCachedSession() throws IOException {
        Document page = tryFetchWithCachedSession();

        if (page == null || page.text().contains("You Have Not Permission")) {
            // cache missing or session expired — log in again
            log.info("Cache Expired!. Logging again");
            cachedSession = login();
            page = cachedSession.url(NOTICES_URL).get();
        }

        if (page.text().contains("You Have Not Permission")) {
            throw new IllegalStateException("FOSMIS login failed — check credentials");
        }

        return page;
    }

    private Document tryFetchWithCachedSession() throws IOException {
        if (cachedSession == null)
            return null;
        return cachedSession.url(NOTICES_URL).get();
    }

    private Connection login() throws IOException {
        Connection session = Jsoup.newSession()
                .userAgent(USER_AGENT)
                .header("Accept-Language", "en-US,en;q=0.9");

        session.url(BASE + "index.php").get();
        log.info("username = {} ,Password = {}",username,pwd);
        session.url(LOGIN_URL)
                .data("uname", username)
                .data("upwd", pwd)
                .method(Connection.Method.POST)
                .execute();

        return session;
    }

}
