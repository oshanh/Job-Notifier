package org.oshanh.jobnotifier.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Job {
    private String position;
    private String companyName;
    private String source;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String location;
}
