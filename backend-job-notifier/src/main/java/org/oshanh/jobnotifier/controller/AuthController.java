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
        // Save the user (will be inactive initially)
        userService.save(userDTO);

        // Dispatch OTP to the requested email
        otpService.generateAndSendOtp(userDTO.getEmail());

        // Return a response carrying just a confirmation message indicating
        // verification is pending.
        return new AuthResponse("OTP_SENT");
    }

    @PostMapping("/verify-registration")
    public AuthResponse verifyRegistration(@RequestBody AuthRequest request) { // Reusing AuthRequest for email/otp
                                                                               // parsing
        boolean isVerified = otpService.validateOtp(request.getEmail(), request.getPassword()); // "password" field
                                                                                                // receives OTP

        if (!isVerified) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Activate the user
        userService.activateUser(request.getEmail());

        // We cannot use authenticationManager.authenticate here dynamically unless they
        // provide raw password,
        // so we manually forge a generic User JWT since they proved they own the email.
        // The safest approach is requiring the raw password again, or manually
        // constructing.
        org.oshanh.jobnotifier.model.User user = userService.findByEmailEntity(request.getEmail());
        String roles = user.getRole().name();
        String token = jwtUtil.generateToken(user.getEmail(), roles);

        return new AuthResponse(token);
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
