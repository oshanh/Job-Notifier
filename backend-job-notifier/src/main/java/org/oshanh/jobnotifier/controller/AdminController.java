package org.oshanh.jobnotifier.controller;

import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users/all")
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/users/add")
    public UserDTO addUser(@RequestBody UserDTO userDTO) {
        return userService.save(userDTO);
    }

    @PutMapping("/users/update")
    public UserDTO updateUser(@RequestBody UserDTO userDTO) {
        return userService.update(userDTO.getEmail(), userDTO);
    }

    @DeleteMapping("/users/delete")
    public void deleteUser(@RequestBody UserDTO userDTO) {
        userService.delete(userDTO.getEmail());
    }

}
