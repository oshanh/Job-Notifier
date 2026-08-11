package org.oshanh.jobnotifier.controller;

import org.oshanh.jobnotifier.dto.UserDTO;
import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.oshanh.jobnotifier.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }


    @PostMapping("add")
    public UserDTO createUser(@RequestBody UserDTO user){
        return userService.save(user);
    }

    @GetMapping("all")
    public List<UserDTO> getUsers(){
        return userService.getUsers();
    }
}
