package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fosmis_user",uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class FosmisUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private boolean isEnabled;


}
