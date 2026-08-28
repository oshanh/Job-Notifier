package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.WebsiteDTO;
import org.oshanh.jobnotifier.exception.AlreadyExistsException;
import org.oshanh.jobnotifier.exception.ResourceNotFoundException;
import org.oshanh.jobnotifier.model.Website;
import org.oshanh.jobnotifier.model.WebsiteURL;
import org.oshanh.jobnotifier.repository.WebsiteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oshanh.jobnotifier.mapper.WebsiteMapper;

@Service
@AllArgsConstructor
public class WebsiteService {
    private final WebsiteRepository websiteRepository;

    public WebsiteDTO save(WebsiteDTO websiteDTO) {
        Website isExist=websiteRepository.findByBaseURL(websiteDTO.getWebsite());
        if(isExist!=null){
            throw new AlreadyExistsException("Website Already Exists");
        }

        Website savedWebsite = websiteRepository.save(WebsiteMapper.mapToEntity(websiteDTO));
        return WebsiteMapper.mapToDTO(savedWebsite);
    }

    public List<WebsiteDTO> getAllWebsites() {
        List<Website> websites = websiteRepository.findAll();
        List<WebsiteDTO> websiteDTOs = new ArrayList<>();
        for (Website website : websites) {
            websiteDTOs.add(WebsiteMapper.mapToDTO(website));
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
            //check duplicated urls
            Set<String> urlSet = new HashSet<>(urls);
            urlSet.addAll(urls);
            for (String url : urlSet) {
                WebsiteURL websiteURL = new WebsiteURL();
                websiteURL.setUrl(url);
                websiteURL.setWebsite(website);
                website.getUrls().add(websiteURL);
            }
        }

        Website savedWebsite = websiteRepository.save(website);
        return WebsiteMapper.mapToDTO(savedWebsite);
    }

    public WebsiteDTO updateWebsite(String baseURL, WebsiteDTO websiteDTO) {
        Website website = websiteRepository.findByBaseURL(baseURL);
        if (website == null) {
            throw new ResourceNotFoundException("Website not found");
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
        return WebsiteMapper.mapToDTO(savedWebsite);
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


}
