package org.oshanh.jobnotifier.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Preference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="uid",unique=true,nullable=false)
    private User user;

    @OneToMany(mappedBy = "preference", cascade = CascadeType.ALL,orphanRemoval=true)
    private List<Keyword> keywords=new ArrayList<>();

    @OneToMany(mappedBy = "preference", cascade = CascadeType.ALL,orphanRemoval=true)
    private List<Website>  websites=new ArrayList<>();

    private String whatsapp_num;
    private String telegram_id;

    private boolean whatsapp_enabled;
    private boolean telegram_enabled;
    private boolean email_enabled;





}