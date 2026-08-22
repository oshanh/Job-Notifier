package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.oshanh.jobnotifier.config.CustomUserDetails;
import org.oshanh.jobnotifier.dto.AuthRequest;
import org.oshanh.jobnotifier.dto.AuthResponse;
import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.service.OtpService;
import org.oshanh.jobnotifier.service.UserService;
import org.oshanh.jobnotifier.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        return getAuthResponse(authentication);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody UserDTO userDTO) {
        if (userService.emailExists(userDTO.getEmail())) {
            throw new RuntimeException("User with this email already exists.");
        }

        // Dispatch OTP to the requested email WITHOUT saving them into the DB yet
        otpService.generateAndSendOtp(userDTO.getEmail());

        // Return a response carrying just a confirmation message indicating
        // verification is pending.
        return new AuthResponse("OTP_SENT");
    }

    @PostMapping("/verify-registration")
    public AuthResponse verifyRegistration(@RequestBody UserDTO request) {
        boolean isVerified = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!isVerified) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // OTP is inherently trusted at this point. Time to permanently persist &
        // activate the user in the database.
        userService.save(request);

        org.oshanh.jobnotifier.model.User user = userService.findByEmailEntity(request.getEmail());
        String roles = user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), roles);

        return new AuthResponse(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        if (!userService.emailExists(email.trim())) {
            // Standard security practice: Don't explicitly reveal if email exists, but we
            // want a good UX.
            return ResponseEntity.badRequest().body(Map.of("message", "Account with this email does not exist"));
        }

        try {
            otpService.generateAndSendOtp(email.trim());
            return ResponseEntity.ok(Map.of("message", "OTP dispatched to your email"));
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "Failed to dispatch email. Please try again."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        String newPassword = payload.get("newPassword");

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required parameters"));
        }

        boolean isVerified = otpService.validateOtp(email.trim(), otp.trim());
        if (!isVerified) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired OTP"));
        }

        userService.resetPassword(email.trim(), newPassword);

        return ResponseEntity.ok(Map.of("message", "Password resetting successfully completed"));
    }

    @NotNull
    private AuthResponse getAuthResponse(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.joining(","));

        String token = jwtUtil.generateToken(userDetails.getUsername(), roles);
        return new AuthResponse(token);
    }

}
