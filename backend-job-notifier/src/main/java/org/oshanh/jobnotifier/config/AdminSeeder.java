package org.oshanh.jobnotifier.config;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(adminEmail);

        if (optionalUser.isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setName("Admin User");

            // Set password from properties
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(User.ROLE.ADMIN);
            admin.setEnabled(true);

            userRepository.save(admin);
            System.out.println("=================================================");
            System.out.println("Admin user created with email: " + adminEmail);
            System.out.println("Temporary password: " + adminPassword);
            System.out.println("=================================================");
        } else {
            // Ensure the user has the ADMIN role if they were previously created as USER
            User existingUser = optionalUser.get();
            if (User.ROLE.ADMIN != existingUser.getRole()) {
                existingUser.setRole(User.ROLE.ADMIN);
                userRepository.save(existingUser);
                System.out.println("Promoted " + adminEmail + " to ADMIN role.");
            }
        }
    }
}
