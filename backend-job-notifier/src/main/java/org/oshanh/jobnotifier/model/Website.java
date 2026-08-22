package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;
import java.util.prefs.Preferences;

@Entity
@Data
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String baseURL;

    @Column(nullable = false)
    private boolean isEnabled;

    @OneToMany(mappedBy = "website", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebsiteURL> urls;

    @ManyToMany(mappedBy = "websites")
    private List<Preference> preferences;

}
