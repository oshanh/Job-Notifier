package org.oshanh.jobnotifier.service;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.FosmisUserDto;
import org.oshanh.jobnotifier.exception.AlreadyExistsException;
import org.oshanh.jobnotifier.exception.ResourceNotFoundException;
import org.oshanh.jobnotifier.exception.UsernameNotFoundException;
import org.oshanh.jobnotifier.model.FosmisUser;
import org.oshanh.jobnotifier.repository.FosmisUserRepository;
import org.springframework.stereotype.Service;
import org.oshanh.jobnotifier.mapper.FosmisUserMapper;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FosmisService {

    private final FosmisUserRepository fosmisUserRepository;

    public List<FosmisUserDto> getAllUsers() {
        return fosmisUserRepository.findAll().stream()
                .map(FosmisUserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void checkUserExists(FosmisUserDto dto) {
        String lowerCaseUsername = dto.getUsername().toLowerCase();
        if (fosmisUserRepository.existsByUsername(lowerCaseUsername)) {
            throw new AlreadyExistsException("Username already exists");
        }
        if (fosmisUserRepository.existsByEmail(dto.getEmail().toLowerCase())) {
            throw new AlreadyExistsException("Email already exists");
        }
    }

    public FosmisUserDto getUserByUsername(String username) {
        String lowerCaseUsername = username.toLowerCase();
        FosmisUser user = fosmisUserRepository.findByUsername(lowerCaseUsername)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "FosmisUser not found with username: " + lowerCaseUsername));
        return FosmisUserMapper.mapToDto(user);
    }

    public FosmisUserDto createUser(FosmisUserDto dto) {
        validateUsername(dto.getUsername());
        String lowerCaseUsername = dto.getUsername().toLowerCase();

        if (fosmisUserRepository.existsByUsername(lowerCaseUsername)) {
            throw new AlreadyExistsException("Username already exists");
        }
        if (fosmisUserRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }

        FosmisUser user = new FosmisUser();
        user.setUsername(lowerCaseUsername);
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setEnabled(true);

        FosmisUser savedUser = fosmisUserRepository.save(user);
        return FosmisUserMapper.mapToDto(savedUser);
    }

    public FosmisUserDto updateUser(String username, FosmisUserDto dto) {
        validateUsername(dto.getUsername());

        String lowerCaseDtoUsername = dto.getUsername().toLowerCase();
        String lowerCasePathUsername = username.toLowerCase();

        FosmisUser user = fosmisUserRepository.findByUsername(lowerCasePathUsername)
                .orElseThrow(
                        () -> new ResourceNotFoundException("FosmisUser not found"));

        if (!user.getUsername().equals(lowerCaseDtoUsername)
                && fosmisUserRepository.existsByUsername(lowerCaseDtoUsername)) {
            throw new AlreadyExistsException("Username already exists");
        }

        if (!user.getEmail().equals(dto.getEmail())
                && fosmisUserRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }

        user.setUsername(lowerCaseDtoUsername);
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);

        FosmisUser updatedUser = fosmisUserRepository.save(user);
        return FosmisUserMapper.mapToDto(updatedUser);
    }

    public void deleteUser(String username) {
        String lowerCaseUsername = username.toLowerCase();
        FosmisUser user = fosmisUserRepository.findByUsername(lowerCaseUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FosmisUser not found with username: " + lowerCaseUsername));
        fosmisUserRepository.delete(user);
    }

    private void validateUsername(String username) {
        if (username == null || !username.matches("(?i)^sc\\d{5}$")) {
            throw new IllegalArgumentException("Invalid username format.");
        }

        int numberPart = Integer.parseInt(username.substring(2));
        if (numberPart < 10000 || numberPart > 18000) {
            throw new UsernameNotFoundException(
                    "The username may not exist yet, or the university membership may have expired.");
        }
    }

}
