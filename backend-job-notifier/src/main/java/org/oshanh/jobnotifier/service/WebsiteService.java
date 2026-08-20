package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.WebsiteDTO;
import org.oshanh.jobnotifier.model.Website;
import org.oshanh.jobnotifier.model.WebsiteURL;
import org.oshanh.jobnotifier.repository.WebsiteRepository;
import org.oshanh.jobnotifier.repository.WebsiteURLRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class WebsiteService {
    private final WebsiteURLRepository websiteURLRepository;
    private final WebsiteRepository websiteRepository;

    public WebsiteDTO save(WebsiteDTO websiteDTO) {
        Website website = new Website();
        website.setBaseURL(websiteDTO.getWebsite());
        website.setEnabled(websiteDTO.isEnabled());
        List<WebsiteURL> websiteURLs = new ArrayList<>();

        if (websiteDTO.getUrl() != null) {
            for (String url : websiteDTO.getUrl()) {
                WebsiteURL websiteURL = new WebsiteURL();
                websiteURL.setUrl(url);
                websiteURL.setWebsite(website);
                websiteURLs.add(websiteURL);
            }
        }
        website.setUrls(websiteURLs);

        Website savedWebsite = websiteRepository.save(website);
        return mapToDTO(savedWebsite);
    }

    public List<WebsiteDTO> getAllWebsites() {
        List<Website> websites = websiteRepository.findAll();
        List<WebsiteDTO> websiteDTOs = new ArrayList<>();
        for (Website website : websites) {
            websiteDTOs.add(mapToDTO(website));
        }
        return websiteDTOs;
    }

    public WebsiteDTO addUrlsToWebsite(WebsiteDTO websiteDTO) {
        String baseURL = websiteDTO.getWebsite();
        List<String> urls = websiteDTO.getUrl();
        Website website = websiteRepository.findByBaseURL(baseURL);

        if (website.getUrls() == null) {
            website.setUrls(new ArrayList<>());
        }

        if (urls != null) {
            for (String url : urls) {
                WebsiteURL websiteURL = new WebsiteURL();
                websiteURL.setUrl(url);
                websiteURL.setWebsite(website);
                website.getUrls().add(websiteURL);
            }
        }

        Website savedWebsite = websiteRepository.save(website);
        return mapToDTO(savedWebsite);
    }

    public WebsiteDTO updateWebsite(String baseURL, WebsiteDTO websiteDTO) {
        Website website = websiteRepository.findByBaseURL(baseURL);
        if (website == null) {
            throw new IllegalArgumentException("Website not found");
        }

        website.setBaseURL(websiteDTO.getWebsite());
        website.setEnabled(websiteDTO.isEnabled());

        website.getUrls().clear();

        if (websiteDTO.getUrl() != null) {
            for (String url : websiteDTO.getUrl()) {
                WebsiteURL websiteURL = new WebsiteURL();
                websiteURL.setUrl(url);
                websiteURL.setWebsite(website);
                website.getUrls().add(websiteURL);
            }
        }

        Website savedWebsite = websiteRepository.save(website);
        return mapToDTO(savedWebsite);
    }

    public void softDeleteWebsite(String baseURL) {
        Website website = websiteRepository.findByBaseURL(baseURL);
        if (website != null) {
            website.setEnabled(false);
            websiteRepository.save(website);
        }
    }

    public void hardDeleteWebsite(String baseURL) {
        Website website = websiteRepository.findByBaseURL(baseURL);
        if (website != null) {
            websiteRepository.delete(website);
        }
    }

    private WebsiteDTO mapToDTO(Website website) {
        WebsiteDTO dto = new WebsiteDTO();
        dto.setWebsite(website.getBaseURL());
        dto.setEnabled(website.isEnabled());
        List<String> urls = new ArrayList<>();
        if (website.getUrls() != null) {
            for (WebsiteURL websiteURL : website.getUrls()) {
                urls.add(websiteURL.getUrl());
            }
        }
        dto.setUrl(urls);
        return dto;
    }
}
