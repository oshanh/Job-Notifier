package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.FosmisUserDto;
import org.oshanh.jobnotifier.service.FosmisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fosmis")
@RequiredArgsConstructor
public class FosmisController {

    private final FosmisService fosmisService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FosmisUserDto>> getAllUsers() {
        return ResponseEntity.ok(fosmisService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<FosmisUserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(fosmisService.getUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<FosmisUserDto> createUser(@RequestBody FosmisUserDto dto) {
        return new ResponseEntity<>(fosmisService.createUser(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{username}")
    public ResponseEntity<FosmisUserDto> updateUser(@PathVariable String username, @RequestBody FosmisUserDto dto) {
        return ResponseEntity.ok(fosmisService.updateUser(username, dto));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        fosmisService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}
