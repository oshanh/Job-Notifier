package org.oshanh.jobnotifier.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.WebsiteDTO;
import org.oshanh.jobnotifier.service.WebsiteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "websites")
@AllArgsConstructor
public class WebsiteController {
    private final WebsiteService websiteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WebsiteDTO saveWebsite(@Valid @RequestBody WebsiteDTO websiteDTO) {
        return websiteService.save(websiteDTO);
    }

    @GetMapping
    public List<WebsiteDTO> getAllWebsites() {
        return websiteService.getAllWebsites();
    }

    @PostMapping("/urls")
    @PreAuthorize("hasRole('ADMIN')")
    public WebsiteDTO addNewUrlsToWebsite(@Valid @RequestBody WebsiteDTO websiteDTO) {
        return websiteService.addUrlsToWebsite(websiteDTO);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WebsiteDTO updateWebsite(@RequestParam("url") String website, @Valid @RequestBody WebsiteDTO websiteDTO) {
        return websiteService.updateWebsite(website, websiteDTO);
    }

    @PatchMapping("/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteWebsite(@RequestParam("url") String website) {
        websiteService.softDeleteWebsite(website);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void hardDeleteWebsite(@RequestParam("url") String website) {
        websiteService.hardDeleteWebsite(website);
    }
}
