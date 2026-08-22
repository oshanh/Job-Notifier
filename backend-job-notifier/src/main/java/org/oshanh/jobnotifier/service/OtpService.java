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
        OtpToken existingToken = otpTokenRepository.findByEmail(email).orElse(null);

        if (existingToken != null && existingToken.getExpirationTime().isAfter(LocalDateTime.now())) {
            log.info("Resending existing OTP to {}", email);
            notificationService.sendOtpEmail(email, existingToken.getOtp());
            return;
        }

        if (existingToken != null) {
            log.info("Existing OTP expired for {}", email);
            otpTokenRepository.delete(existingToken);
        }

        // Generate a 6-digit cryptographically secure random OTP
        String otp = String.valueOf(100000 + secureRandom.nextInt(900000));

        // Store token valid for 15 minutes
        OtpToken newOtpToken = new OtpToken(
                email,
                otp,
                LocalDateTime.now().plusMinutes(15)
        );

        otpTokenRepository.save(newOtpToken);

        // Dispatch email
        log.info("Sending new OTP to {}", email);
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
