package org.oshanh.jobnotifier.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserDTO save(UserDTO user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User u = new User();
        u.setEmail(user.getEmail());
        u.setName(user.getName());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        u.setRole(User.ROLE.USER);
        u.setEnabled(true);

        User su = userRepository.save(u);
        UserDTO savedUser = new UserDTO();
        savedUser.setEmail(su.getEmail());
        savedUser.setName(su.getName());
        return savedUser;

    }

    public User findByEmailEntity(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User missing in DB context"));
    }

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> list = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setName(user.getName());
            userDTO.setRole(user.getRole());
            userDTO.setEnabled(user.isEnabled());
            list.add(userDTO);
        }
        return list;
    }

    public UserDTO update(String email, UserDTO userDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(userDTO.getName());
        // Since we identify by email, updating the email might be risky, but we apply
        // it here if provided.
        // It might be safer to keep the old email if they don't explicitly change it.
        if (userDTO.getEmail() != null) {
            if (!userDTO.getEmail().equals(email) && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("User with this email already exists");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getRole() != null)
            user.setRole(userDTO.getRole());
        user.setEnabled(userDTO.isEnabled());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        User updated = userRepository.save(user);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setEmail(updated.getEmail());
        responseDTO.setName(updated.getName());
        responseDTO.setRole(updated.getRole());
        responseDTO.setEnabled(updated.isEnabled());
        return responseDTO;
    }

    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }

    public UserDTO updateProfileDetails(String email, UserDTO dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty() && !dto.getEmail().equals(email)) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("User with this email already exists");
            }
            user.setEmail(dto.getEmail());
            log.info("email from dto =  {}", dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (dto.getOldPassword() == null || !passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid old password");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Updated profile details for user with email {}", updatedUser.getEmail());

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setEmail(updatedUser.getEmail());
        updatedDTO.setName(updatedUser.getName());
        updatedDTO.setRole(updatedUser.getRole());
        updatedDTO.setEnabled(updatedUser.isEnabled());
        return updatedDTO;
    }
}
