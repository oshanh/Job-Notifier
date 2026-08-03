package org.oshanh.jobnotifier.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.WebsiteDTO;
import org.oshanh.jobnotifier.service.WebsiteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "websites")
@AllArgsConstructor
public class WebsiteController {
    private final WebsiteService websiteService;

    @PostMapping
    public WebsiteDTO saveWebsite(@Valid @RequestBody WebsiteDTO websiteDTO){
        return websiteService.save(websiteDTO);
    }
    @GetMapping
    public List<WebsiteDTO> getAllWebsites(){
        return websiteService.getAllWebsites();
    }

    @PostMapping("/urls")
    public WebsiteDTO addNewUrlsToWebsite(@Valid @RequestBody WebsiteDTO websiteDTO) {
        return websiteService.addUrlsToWebsite(websiteDTO);
    }

    
}
