package com.productivity_mangement.productivity.entity;

import com.productivity_mangement.productivity.DTO.IntegrationProvider;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "integration_accounts")
public class IntegrationAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    private IntegrationProvider provider;

    @Column(length = 2048)
    private String accessToken;

    @Column(length = 2048)
    private String refreshToken;

    private String accountLabel;
}

