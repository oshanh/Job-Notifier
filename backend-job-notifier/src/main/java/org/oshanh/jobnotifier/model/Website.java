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

    private String baseURL;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isEnabled = true;

    @OneToMany(mappedBy = "website", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebsiteURL> urls;

    @ManyToOne
    @JoinColumn(name = "preference_id")
    private Preference preference;

}
