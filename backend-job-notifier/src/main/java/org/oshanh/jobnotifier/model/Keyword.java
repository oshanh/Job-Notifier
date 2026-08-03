package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @ManyToOne
    @JoinColumn(name="uid")
    private Preference preference;


}
