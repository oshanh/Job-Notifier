package org.oshanh.jobnotifier.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PreferenceDTO {

    private Long uid;
    private String email;
    private List<String> keyword=new ArrayList<>();
    private String whatsapp_num;
    private String telegram_id;
    private boolean whatsapp_enabled;
    private boolean telegram_enabled;
    private boolean email_enabled;

}
