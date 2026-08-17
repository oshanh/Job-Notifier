package org.oshanh.jobnotifier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FosmisUserDto {
    @NotBlank(message = "Username is required!")
    private String username;
    @Email(message = "Invalid email address")
    private String email;
    private boolean isEnabled;
}
