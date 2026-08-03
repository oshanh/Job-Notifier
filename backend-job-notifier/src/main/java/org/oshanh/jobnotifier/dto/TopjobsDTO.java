package org.oshanh.jobnotifier.dto;

import lombok.Data;
import java.util.Date;

@Data
public class TopjobsDTO {
    private int refNo;
    private String jobTitle;
    private String companyName;
    private Date openingDate;
    private Date closingDate;
    private String location;
}
