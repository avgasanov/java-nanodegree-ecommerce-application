package com.example.demo.security;

import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {
    private static final long serialVersionUID = 1L;
    public CustomUser(com.example.demo.model.persistence.User user) {
        super(user.getUsername(), user.getPassword(), user.getGrantedAuthoritiesList());
    }
}
