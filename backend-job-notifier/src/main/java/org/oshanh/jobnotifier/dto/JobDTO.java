package org.oshanh.jobnotifier.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobDTO {
    private String position;
    private String companyName;
    private String source;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String location;
}
