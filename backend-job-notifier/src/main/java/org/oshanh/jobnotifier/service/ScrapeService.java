package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.oshanh.jobnotifier.dto.FosmisEmailMessage;
import org.oshanh.jobnotifier.dto.JobEmailMessage;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.mapper.JobMapper;
import org.oshanh.jobnotifier.model.*;
import org.oshanh.jobnotifier.repository.*;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final EmailProducer emailProducer;
    private final FosmisUserRepository fosmisUserRepository;

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

    /*--------------------------------------------
    
                 Scrape Topjobs.lk
    
     ---------------------------------------------*/
    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public List<JobDTO> scrapeTopjobs() {
        log.info("scraping Topjobs");

        Website website = websiteRepository.findByBaseURL("https://www.topjobs.lk");
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
                log.error("Network timeout while scraping TopJobs: {}", e.getMessage());
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
                prefService.sendEmailForPreference(savedJobDTOS, website);
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

    /*--------------------------------------------
    
                  Scrape airport.lk
    
    ---------------------------------------------*/
    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public List<JobDTO> scrapeAirportJobs() {
        log.info("scraping AirportJobs");
        Website website = websiteRepository.findByBaseURL("https://www.airport.lk");
        List<String> URLs = new ArrayList<>();
        if (website != null) {
            URLs = website.getUrls().stream().map(WebsiteURL::getUrl).toList();
        } else {
            // Fallback for testing if database is not set up
            URLs.add("https://www.airport.lk/aasl/careers/careers");
        }

        List<Airportjobs> existingJobs = airportjobsRepository.findAll();
        List<Airportjobs> scrapedAirportjobs = new ArrayList<>();

        for (String u : URLs) {
            try {
                Document doc = Jsoup.connect(u).sslSocketFactory(socketFactory()).get();
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

                        if (!closingDateStr.equalsIgnoreCase("N/A") && !closingDateStr.isEmpty()) {
                            try {
                                ajob.setClosingDate(LocalDate.parse(closingDateStr));
                            } catch (DateTimeParseException ignored) {
                            }
                        }

                        scrapedAirportjobs.add(ajob);
                    }
                }
            } catch (IOException e) {
                log.error("Network timeout while scraping Airports: {}", e.getMessage());
            }

        }

        // filter new jobs using an O(1) HashSet lookup for high performance
        Set<String> existingSignatures = existingJobs.stream()
                .map(j -> j.getJobUrl() + "|" + j.getPosition() + "|" + j.getClosingDate())
                .collect(java.util.stream.Collectors.toSet());

        Set<Airportjobs> newAirportJobs = new HashSet<>();
        for (Airportjobs scraped : scrapedAirportjobs) {
            String signature = scraped.getJobUrl() + "|" + scraped.getPosition() + "|" + scraped.getClosingDate();
            // Only add if this unique composite signature does not already exist in the database
            if (!existingSignatures.contains(signature)) {
                newAirportJobs.add(scraped);
            }
        }

        airportjobsRepository.saveAll(newAirportJobs);
        List<JobDTO> jobDTOS = new ArrayList<>(JobMapper.toAirportJobsToJob(newAirportJobs));

        if (!jobDTOS.isEmpty()) {
            prefService.sendEmailForPreference(jobDTOS, website);
        }
        return jobDTOS;
    }

    private javax.net.ssl.SSLSocketFactory socketFactory() {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    /*--------------------------------------------
    
              Scrape FOSMIS Notifications
    
    ---------------------------------------------*/
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

    // @Scheduled(cron = "0 0,30 8-17 * * MON-FRI")
    //@Scheduled(fixedRate = 120, timeUnit = TimeUnit.MINUTES)
    public void checkForNewNotices() throws IOException {
        Document page = fetchNoticesPageWithCachedSession();
        List<FosmisNotice> scraped = parse(page);

        log.info("Scraped {} Notices", scraped.size());

        // Oldest new notice emails first
        Collections.reverse(scraped);

        // Deduplicate by link to prevent constraint violations if the HTML contains
        // duplicates
        List<FosmisNotice> uniqueScraped = new ArrayList<>();
        Set<String> seenLinks = new HashSet<>();
        for (FosmisNotice notice : scraped) {
            if (seenLinks.add(notice.getLink())) {
                uniqueScraped.add(notice);
            }
        }
        scraped = uniqueScraped;

        // Highly optimized memory approach: Fetch existing DB links and check in memory
        Set<String> existingLinks = fosmisNoticeRepository.findAllLinks();

        List<FosmisNotice> newNotices = scraped.stream()
                .filter(notice -> !existingLinks.contains(notice.getLink()))
                .toList();

        if (newNotices.isEmpty()) {
            return;
        }
        log.info("Found {} new notices", newNotices.size());

        fosmisNoticeRepository.saveAll(newNotices);

        // SAFETY MEASURE: If the database is completely empty (first startup),
        // do NOT send 6000+ emails! Just populate the database silently.
        if (existingLinks.isEmpty() && newNotices.size() > 100) {
            log.info("Initial historical data load complete. Skipping email notifications to prevent spam.");
            return;
        }

        // Get all FOSMIS users
        List<String> emails = fosmisUserRepository.findAllEnabledEmails();

        log.info(
                "Found {} FOSMIS notification users",
                emails.size());

        // Notice first → Email second
        for (FosmisNotice notice : newNotices) {

            for (String email : emails) {

                FosmisEmailMessage message = new FosmisEmailMessage(
                        notice.getId(),
                        email,
                        notice.getTitle(),
                        notice.getPublishedAt(),
                        notice.getLink());

                emailProducer.sendFosmisEmail(message);
            }
        }

        log.info(
                "Queued {} email notifications",
                newNotices.size() * emails.size());
    }

    public Document fetchNoticesPageWithCachedSession() throws IOException {
        Document page = tryFetchWithCachedSession();

        if (page == null || page.text().contains("You Have Not Permission")) {
            // cache missing or session expired — log in again
            log.info("Initializing active FOSMIS session...");
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

        session.url(LOGIN_URL)
                .data("uname", username)
                .data("upwd", pwd)
                .method(Connection.Method.POST)
                .execute();

        return session;
    }

}
