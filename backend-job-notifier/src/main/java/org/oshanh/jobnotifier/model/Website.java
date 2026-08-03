package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;


@Entity
@Data
public class Website {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseURL;

    @OneToMany(mappedBy = "website" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebsiteURL> urls;

}
