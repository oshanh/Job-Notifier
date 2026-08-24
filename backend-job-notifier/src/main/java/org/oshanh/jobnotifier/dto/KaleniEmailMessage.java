package org.oshanh.jobnotifier.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record KaleniEmailMessage(
        String title,
        LocalDate deadline,
        String portalUrl,
        String email,
        String department,
        String employmentType,
        String salary,
        String description,
        String advertisementUrl,
        String applicationUrl) implements Serializable {
}
