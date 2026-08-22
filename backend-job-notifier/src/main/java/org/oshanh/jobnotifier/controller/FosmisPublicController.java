package org.oshanh.jobnotifier.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.FosmisUserDto;
import org.oshanh.jobnotifier.service.FosmisService;
import org.oshanh.jobnotifier.service.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fosmis-notification")
@RequiredArgsConstructor
public class FosmisPublicController {

    private final FosmisService fosmisService;
    private final OtpService otpService;

    @PostMapping
    public ResponseEntity<Map<String, String>> subscribe(@Valid @RequestBody FosmisUserDto dto) {
        fosmisService.checkUserExists(dto);

        // Dispatch OTP to the requested email without saving into the DB yet
        otpService.generateAndSendOtp(dto.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP_SENT"));
    }

    @PostMapping("/verify")
    public ResponseEntity<FosmisUserDto> verify(@Valid @RequestBody FosmisUserDto dto) {
        if (dto.getOtp() == null || dto.getOtp().isBlank()) {
            throw new RuntimeException("OTP is required for verification");
        }
        boolean isVerified = otpService.validateOtp(dto.getEmail(), dto.getOtp());
        if (!isVerified) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        dto.setIsEnabled(true);
        return new ResponseEntity<>(fosmisService.createUser(dto), HttpStatus.CREATED);
    }
}
