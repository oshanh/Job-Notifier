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

        //DTO to entity
        Website website = new Website();
        website.setBaseURL(websiteDTO.getWebsite());
        List<WebsiteURL> websiteURLs = new ArrayList<>();

        for (String url : websiteDTO.getUrl()){
            WebsiteURL websiteURL = new WebsiteURL();
            websiteURL.setUrl(url);
            websiteURL.setWebsite(website);
            websiteURLs.add(websiteURL);
        }
        website.setUrls(websiteURLs);

        //save
        Website savedWebsite = websiteRepository.save(website);

        //return DTO
        WebsiteDTO savedWebsiteDTO = new WebsiteDTO();
        savedWebsiteDTO.setWebsite(savedWebsite.getBaseURL());
        List<String> savedWebsiteURLs = new ArrayList<>();
        for (WebsiteURL websiteURL : savedWebsite.getUrls()) {
            savedWebsiteURLs.add(websiteURL.getUrl());
        }
        savedWebsiteDTO.setUrl(savedWebsiteURLs);
        return savedWebsiteDTO;

    }

    public List<WebsiteDTO> getAllWebsites() {
        List<Website>  websites = websiteRepository.findAll();
        List<WebsiteDTO> websiteDTOs= new ArrayList<>();
        for (Website website : websites) {
            WebsiteDTO websiteDTO = new WebsiteDTO();
            websiteDTO.setWebsite(website.getBaseURL());
            List<String> websiteURLs = website.getUrls().stream().map(WebsiteURL::getUrl).toList();
            websiteDTO.setUrl(websiteURLs);
            websiteDTOs.add(websiteDTO);
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

        for (String url : urls) {
            WebsiteURL websiteURL = new WebsiteURL();
            websiteURL.setUrl(url);
            websiteURL.setWebsite(website);
            website.getUrls().add(websiteURL);
        }

        Website savedWebsite = websiteRepository.save(website);

        WebsiteDTO savedWebsiteDTO = new WebsiteDTO();
        savedWebsiteDTO.setWebsite(savedWebsite.getBaseURL());
        savedWebsiteDTO.setUrl(savedWebsite.getUrls().stream().map(WebsiteURL::getUrl).toList());
        return savedWebsiteDTO;
    }


    
}
