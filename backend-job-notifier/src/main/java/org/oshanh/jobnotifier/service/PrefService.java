package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.dto.PreferenceDTO;
import org.oshanh.jobnotifier.model.Keyword;
import org.oshanh.jobnotifier.model.Preference;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.PrefRepository;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class PrefService {
    private final PrefRepository prefRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PreferenceDTO findByEmail(String email) {
        PreferenceDTO preferenceDTO = new PreferenceDTO();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }
        Preference pref = prefRepository.findByUser_Id(user.getId());

        if (pref == null) {
            return null;
        }

        preferenceDTO.setEmail(pref.getUser().getEmail());

        List<String> keywords = new ArrayList<>();
        for (Keyword s : pref.getKeywords()) {
            keywords.add(s.getKeyword());
        }

        preferenceDTO.setKeyword(keywords);
        preferenceDTO.setUid(pref.getUser().getId());
        preferenceDTO.setWhatsapp_num(pref.getWhatsapp_num());
        preferenceDTO.setTelegram_id(pref.getTelegram_id());
        preferenceDTO.setWhatsapp_enabled(pref.isWhatsapp_enabled());
        preferenceDTO.setTelegram_enabled(pref.isTelegram_enabled());
        preferenceDTO.setEmail_enabled(pref.isEmail_enabled());

        return preferenceDTO;
    }

    public PreferenceDTO save(PreferenceDTO preferenceDTO) {

        Preference pref = new Preference();
        List<Keyword> keywords = new ArrayList<>();

        User user = userRepository.findByEmail(preferenceDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (String s : preferenceDTO.getKeyword()) {
            Keyword keyword = new Keyword();
            keyword.setKeyword(s);
            keyword.setPreference(pref);
            keywords.add(keyword);

        }

        pref.setUser(user);
        pref.setKeywords(keywords);
        pref.setWhatsapp_num(preferenceDTO.getWhatsapp_num());
        pref.setTelegram_id(preferenceDTO.getTelegram_id());
        pref.setWhatsapp_enabled(preferenceDTO.isWhatsapp_enabled());
        pref.setTelegram_enabled(preferenceDTO.isTelegram_enabled());
        pref.setEmail_enabled(preferenceDTO.isEmail_enabled());

        Preference savedPref = prefRepository.save(pref);

        PreferenceDTO savedPreferenceDTO = new PreferenceDTO();
        savedPreferenceDTO.setEmail(savedPref.getUser().getEmail());
        savedPreferenceDTO.setUid(savedPref.getUser().getId());
        savedPreferenceDTO.setWhatsapp_num(savedPref.getWhatsapp_num());
        savedPreferenceDTO.setTelegram_id(savedPref.getTelegram_id());
        savedPreferenceDTO.setWhatsapp_enabled(savedPref.isWhatsapp_enabled());
        savedPreferenceDTO.setTelegram_enabled(savedPref.isTelegram_enabled());
        savedPreferenceDTO.setEmail_enabled(savedPref.isEmail_enabled());
        savedPreferenceDTO.setKeyword(savedPref.getKeywords().stream().map(Keyword::getKeyword).toList());
        return savedPreferenceDTO;
    }

    public PreferenceDTO update(PreferenceDTO preferenceDTO) {
        User user = userRepository.findByEmail(preferenceDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Preference pref = prefRepository.findByUser_Id(user.getId());
        if (pref == null) {
            throw new RuntimeException("Preference not found");
        }

        pref.getKeywords().clear();
        for (String s : preferenceDTO.getKeyword()) {
            Keyword keyword = new Keyword();
            keyword.setKeyword(s);
            keyword.setPreference(pref);
            pref.getKeywords().add(keyword);
        }

        pref.setWhatsapp_num(preferenceDTO.getWhatsapp_num());
        pref.setTelegram_id(preferenceDTO.getTelegram_id());
        pref.setWhatsapp_enabled(preferenceDTO.isWhatsapp_enabled());
        pref.setTelegram_enabled(preferenceDTO.isTelegram_enabled());
        pref.setEmail_enabled(preferenceDTO.isEmail_enabled());

        Preference savedPref = prefRepository.save(pref);

        PreferenceDTO savedPreferenceDTO = new PreferenceDTO();
        savedPreferenceDTO.setEmail(savedPref.getUser().getEmail());
        savedPreferenceDTO.setKeyword(savedPref.getKeywords().stream().map(Keyword::getKeyword).toList());
        return savedPreferenceDTO;
    }

    public void delete(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Preference pref = prefRepository.findByUser_Id(user.getId());
        if (pref != null) {
            prefRepository.delete(pref);
        } else {
            throw new RuntimeException("Preference not found");
        }
    }

    @Transactional(readOnly = true)
    public void sendEmailForPreference(List<JobDTO> newJobDTOS) {
        List<Preference> preferences = prefRepository.findAll();

        for (Preference pref : preferences) {
            // Respect the user's email notification preference

            // skip for development
            // if (!pref.isEmail_enabled()) {
            // continue;
            // }

            String email = pref.getUser().getEmail();

            // Collect all matching jobs across all keywords (de-duplicated)
            Set<JobDTO> matchedJobDTOS = new LinkedHashSet<>();
            for (Keyword keyword : pref.getKeywords()) {
                String kw = keyword.getKeyword().trim().toLowerCase();
                newJobDTOS.forEach(j -> {
                    if (j.getPosition() != null && j.getPosition().toLowerCase().contains(kw)) {
                        matchedJobDTOS.add(j);
                    }
                });
            }

            if (!matchedJobDTOS.isEmpty()) {
                try {
                    notificationService.sendNewJobPostingsNotification(email, new ArrayList<>(matchedJobDTOS));
                } catch (Exception e) {
                    // Log and continue — one failed send shouldn't block other users
                    log.error("Error sending job postings notification", e);
                }
            }
        }
    }

}
