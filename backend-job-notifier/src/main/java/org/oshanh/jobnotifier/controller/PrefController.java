package org.oshanh.jobnotifier.controller;

import lombok.AllArgsConstructor;
import org.oshanh.jobnotifier.dto.PreferenceDTO;
import org.oshanh.jobnotifier.service.PrefService;
import org.oshanh.jobnotifier.service.ScrapeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "pref")
@AllArgsConstructor
public class PrefController {
    private final PrefService prefService;

    @GetMapping
    public ResponseEntity<?> findByUid(@RequestParam String email) {
        PreferenceDTO preferenceDTO = prefService.findByEmail(email);
        if (preferenceDTO != null) {
            return new ResponseEntity<>(preferenceDTO, HttpStatus.OK);

        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public PreferenceDTO save(@RequestBody PreferenceDTO pref) {
        return prefService.save(pref);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody PreferenceDTO pref) {
        try {
            PreferenceDTO updatedPref = prefService.update(pref);
            return new ResponseEntity<>(updatedPref, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String email) {
        try {
            prefService.delete(email);
            return new ResponseEntity<>("Preference deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
