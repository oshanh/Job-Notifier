package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kaleni_uni_jobs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_kaleni_uni_external_id", columnNames = "vacancy_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KaleniUniJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // External vacancy ID
    // Example:
    // senior-lecturer-on-contract-chemistry
    // =====================================================

    @Column(name = "vacancy_id", nullable = false, length = 500)
    private String externalId;

    // =====================================================
    // Job information
    // =====================================================

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String department;

    @Column(name = "employment_type", length = 100)
    private String employmentType;

    // =====================================================
    // Salary
    // =====================================================

    @Column(columnDefinition = "TEXT")
    private String salary;

    // =====================================================
    // Deadline
    // =====================================================

    @Column(name = "deadline")
    private LocalDate deadline;

    // =====================================================
    // Application information
    // =====================================================

    @Column(name = "application_email", length = 255)
    private String applicationEmail;

    @Column(name = "application_subject", columnDefinition = "TEXT")
    private String applicationSubject;

    // =====================================================
    // External links
    // =====================================================

    @Column(name = "advertisement_url", columnDefinition = "TEXT")
    private String advertisementUrl;

    @Column(name = "application_url", columnDefinition = "TEXT")
    private String applicationUrl;

    @Column(name = "portal_url", columnDefinition = "TEXT")
    private String portalUrl;

    // =====================================================
    // General source URL
    // =====================================================

    @Column(name = "external_url", columnDefinition = "TEXT")
    private String externalUrl;

    // =====================================================
    // Source
    // =====================================================

    @Column(nullable = false, length = 255)
    private String company;

    @Column(length = 255)
    private String location;

    @Column(length = 100)
    private String source;

    // =====================================================
    // Timestamps
    // =====================================================

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =====================================================
    // Automatically set timestamps
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}