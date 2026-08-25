package org.oshanh.jobnotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobEmailMessage implements Serializable {
    private String email;
    private String website;
    private List<JobDTO> jobs;
    private List<KaleniUniJobDTO> kaleniUniJobs;

    public JobEmailMessage(String email, List<KaleniUniJobDTO> kaleniUniJobs) {
        this.email = email;
        this.kaleniUniJobs = kaleniUniJobs;
        this.website = "Kaleniya University"; // optional context
    }

    public JobEmailMessage(String email, String website, List<JobDTO> jobs) {
        this.email = email;
        this.website = website;
        this.jobs = jobs;
    }
}
