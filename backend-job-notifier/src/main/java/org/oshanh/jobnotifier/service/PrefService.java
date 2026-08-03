package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.Job;
import org.oshanh.jobnotifier.dto.PreferenceDTO;
import org.oshanh.jobnotifier.model.Keyword;
import org.oshanh.jobnotifier.model.Preference;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.PrefRepository;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PrefService {
    private final PrefRepository prefRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PreferenceDTO findByEmail(String email) {
        PreferenceDTO preferenceDTO = new PreferenceDTO();
        User user = userRepository.findByEmail(email).orElse(null);
        if(user==null){
            return null;
        }
        Preference pref= prefRepository.findByUser_Id(user.getId());

        preferenceDTO.setEmail(pref.getUser().getEmail());

        List<String> keywords=new ArrayList<>();
        for (Keyword s:pref.getKeywords()){
            keywords.add(s.getKeyword());
        }

        preferenceDTO.setKeyword(keywords);

        return preferenceDTO;
    }

    public PreferenceDTO save(PreferenceDTO preferenceDTO) {

        Preference pref = new Preference();
        List<Keyword> keywords = new ArrayList<>();

        User user = userRepository.findByEmail(preferenceDTO.getEmail()).
                    orElseThrow(()->new RuntimeException("User not found"));

        for(String s : preferenceDTO.getKeyword()) {
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

        Preference savedPref= prefRepository.save(pref);

        PreferenceDTO savedPreferenceDTO = new PreferenceDTO();
        savedPreferenceDTO.setEmail(savedPref.getUser().getEmail());
        savedPreferenceDTO.setKeyword(savedPref.getKeywords().stream().map(Keyword::getKeyword).toList());
        return savedPreferenceDTO;
    }

    public void sendEmailForPreference(List<Job> newJobs) {
        List<Preference> preferences=prefRepository.findAll();
        for (Preference pref : preferences) {
            String email=pref.getUser().getEmail();

            List<String> keywords=new ArrayList<>();
            for (Keyword keyword : pref.getKeywords()) {
                keywords.add(keyword.getKeyword());
                List<Job> matchedJObs=new ArrayList<>();
                newJobs.forEach(j->{

                    if(j.getPosition().equals(keyword.getKeyword()))
                        matchedJObs.add(j);
                });
                matchedJObs.forEach(j->{
                    System.out.println(j.getPosition());
                });

                if(!matchedJObs.isEmpty()) {
                    notificationService.sendNewJobPostingsNotification(email, matchedJObs);
                }
                else{
                    System.out.println("\n\nNo matching jobs found\n\n");
                }

            }



        }
    }

}
