package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.model.OtpToken;
import org.oshanh.jobnotifier.repository.OtpTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final NotificationService notificationService;
    private final SecureRandom secureRandom = new SecureRandom();

    public void generateAndSendOtp(String email) {
        // Clean up any existing tokens for this email to prevent collisions
        otpTokenRepository.deleteByEmail(email);

        // Generate 6-digit cryptographic remote random
        int otpValue = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpValue);

        // Store token valid for 15 minutes
        OtpToken otpToken = new OtpToken(email, otp, LocalDateTime.now().plusMinutes(15));
        otpTokenRepository.save(otpToken);

        // Dispatch Email
        log.info("Dispatching email OTP to {}", email);
        notificationService.sendOtpEmail(email, otp);
    }

    public boolean validateOtp(String email, String otp) {
        return otpTokenRepository.findByEmail(email)
                .map(token -> {
                    if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
                        log.warn("OTP expired for email {}", email);
                        return false;
                    }
                    boolean isValid = token.getOtp().equals(otp);
                    if (isValid) {
                        // OTP is a one-time use token, destroy upon successful validation
                        otpTokenRepository.deleteByEmail(email);
                    }
                    return isValid;
                })
                .orElse(false);
    }
}
