package com.productivity_mangement.productivity.entity;

import jakarta.persistence.*;

@Entity
public class SlackToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(length = 1000)
    private String accessToken;

    private String teamName;

    public Long getId() { return id; }

    public String getEmail() { return email; }

    public String getAccessToken() { return accessToken; }

    public String getTeamName() { return teamName; }

    public void setEmail(String email) { this.email = email; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public void setTeamName(String teamName) { this.teamName = teamName; }
}