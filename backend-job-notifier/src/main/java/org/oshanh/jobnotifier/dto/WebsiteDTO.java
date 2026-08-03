package org.oshanh.jobnotifier.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class WebsiteDTO {

    @NotBlank(message = "website is required")
    private String website;
    private List<String> url;

}
