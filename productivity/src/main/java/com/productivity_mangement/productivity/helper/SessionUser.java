package com.productivity_mangement.productivity.helper;


import org.springframework.stereotype.Component;

@Component
public class SessionUser {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

