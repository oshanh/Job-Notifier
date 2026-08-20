package org.oshanh.jobnotifier.dto;

import lombok.Data;

import org.oshanh.jobnotifier.model.User;

@Data
public class UserDTO {
    private String email;
    private String name;
    private String password;
    private String oldPassword;
    private String token;
    private User.ROLE role;
    private boolean enabled;
}
