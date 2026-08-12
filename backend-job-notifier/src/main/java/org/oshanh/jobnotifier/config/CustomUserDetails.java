package org.oshanh.jobnotifier.config;

import org.jetbrains.annotations.NotNull;
import org.oshanh.jobnotifier.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }


    @Override
    public String getUsername() {
        return user.getEmail();
    }
    @Override
    public String getPassword(){
        return user.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
    }
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
