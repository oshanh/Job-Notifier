package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "website_url")
public class WebsiteURL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "website_id")
    private Website website;

    private String url;
}
