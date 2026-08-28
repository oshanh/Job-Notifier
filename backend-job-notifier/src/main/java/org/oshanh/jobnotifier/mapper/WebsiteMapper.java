package org.oshanh.jobnotifier.mapper;

import org.oshanh.jobnotifier.dto.WebsiteDTO;
import org.oshanh.jobnotifier.model.Website;
import org.oshanh.jobnotifier.model.WebsiteURL;

import java.util.ArrayList;
import java.util.List;

public class WebsiteMapper {

    public static WebsiteDTO mapToDTO(Website website) {
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
    public static Website mapToEntity(WebsiteDTO websiteDTO) {
        Website website = new Website();
        website.setBaseURL(websiteDTO.getWebsite());
        website.setEnabled(true);
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
        return website;
    }
}
