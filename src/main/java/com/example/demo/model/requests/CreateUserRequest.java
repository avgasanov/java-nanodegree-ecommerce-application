package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

public class CreateUserRequest {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String confirmPassword;

    @JsonIgnore
    private int salt = new Random().nextInt();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public int getSalt() {
        return salt;
    }
}
