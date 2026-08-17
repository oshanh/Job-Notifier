package org.oshanh.jobnotifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobEmailMessage implements Serializable {
    private String email;
    private List<JobDTO> jobs;
}
