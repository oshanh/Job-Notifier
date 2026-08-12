package org.oshanh.jobnotifier.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String password;
    private String role;
    private boolean enabled;

    @OneToOne(mappedBy="user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Preference preference;

    public enum ROLE{
        ADMIN,
        USER
    }
}


