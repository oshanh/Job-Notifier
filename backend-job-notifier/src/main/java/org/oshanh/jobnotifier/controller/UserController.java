package org.oshanh.jobnotifier.controller;

import org.oshanh.jobnotifier.model.User;
import org.oshanh.jobnotifier.repository.UserRepository;
import org.oshanh.jobnotifier.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }


    @PostMapping
    public void createUser(@RequestBody User user){
        userService.save(user);
    }
}
