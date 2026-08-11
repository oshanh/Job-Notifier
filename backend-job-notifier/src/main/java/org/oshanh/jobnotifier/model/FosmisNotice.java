package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "fosmis_notices", uniqueConstraints = @UniqueConstraint(columnNames = "link"))
@Data
public class FosmisNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime publishedAt;

    @Column(length = 1000)
    private String link;          // absolute URL — this is our uniqueness key

    private LocalDateTime notifiedAt;

}