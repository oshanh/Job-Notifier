package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.config.CustomUserDetails;
import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.service.OtpService;
import org.oshanh.jobnotifier.service.UserService;
import org.oshanh.jobnotifier.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @GetMapping("/me")
    public UserDTO getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            currentEmail = ((CustomUserDetails) auth.getPrincipal()).getUsername();
        } else if (auth != null && auth.getPrincipal() instanceof String) {
            currentEmail = (String) auth.getPrincipal();
        }

        if (currentEmail == null) {
            throw new RuntimeException("Unauthorized: Unable to discern profile context");
        }

        User user = userService.findByEmailEntity(currentEmail);
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        return dto;
    }

    @PutMapping("/me")
    public UserDTO updateMyProfile(@RequestBody UserDTO userDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            currentEmail = ((CustomUserDetails) auth.getPrincipal()).getUsername();
        } else if (auth != null && auth.getPrincipal() instanceof String) {
            currentEmail = (String) auth.getPrincipal();
        }

        if (currentEmail == null) {
            throw new RuntimeException("Unauthorized: Unable to discern profile context");
        }

        // Only mapping allowed fields: name and password
        // Roles and enabled status map bypass is enforced downstream or we can enforce
        // it here.
        // For security, an explicitly mapped constrained update is best. We will use
        // the main update, but we guarantee it operates on the user's authentic email.

        // Ensure the email they are updating belongs to the session!
        UserDTO updated = userService.updateProfileDetails(currentEmail, userDTO);

        // If the email was successfully changed, their old token is dead. Wire a new
        // one!
        if (updated.getEmail() != null && !updated.getEmail().equals(currentEmail)) {
            String newToken = jwtUtil.generateToken(updated.getEmail(), "ROLE_" + updated.getRole().name());
            updated.setToken(newToken);
        }

        return updated;
    }

    @PostMapping("/request-email-change")
    public String requestEmailChange(@RequestBody Map<String, String> payload) {
        String newEmail = payload.get("newEmail");
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("New email is required");
        }

        if (userService.emailExists(newEmail.trim())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Dispatch OTP to the intended new email address to verify ownership
        otpService.generateAndSendOtp(newEmail.trim());
        return "{\"message\": \"OTP sent to new email address\"}";
    }

    @PostMapping("/verify-email-change")
    public UserDTO verifyEmailChange(@RequestBody Map<String, String> payload) {
        String newEmail = payload.get("newEmail");
        String otp = payload.get("otp");

        boolean isVerified = otpService.validateOtp(newEmail, otp);
        if (!isVerified) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            currentEmail = ((CustomUserDetails) auth.getPrincipal()).getUsername();
        } else if (auth != null && auth.getPrincipal() instanceof String) {
            currentEmail = (String) auth.getPrincipal();
        }

        // OTP verified successfully for the newEmail address. Formulate a DTO to
        // trigger updateProfileDetails
        UserDTO proxyDto = new UserDTO();
        proxyDto.setEmail(newEmail);

        // This will reuse the existing logic to check for collisions, save the new
        // email, and generate a new JWT
        UserDTO updated = userService.updateProfileDetails(currentEmail, proxyDto);

        // If the email was successfully changed, wire a new token!
        if (updated.getEmail() != null && !updated.getEmail().equals(currentEmail)) {
            String newToken = jwtUtil.generateToken(updated.getEmail(), "ROLE_" + updated.getRole().name());
            updated.setToken(newToken);
        }

        return updated;
    }
}
