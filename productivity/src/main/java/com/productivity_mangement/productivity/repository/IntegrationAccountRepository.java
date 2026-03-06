package com.productivity_mangement.productivity.repository;

import com.productivity_mangement.productivity.DTO.IntegrationProvider;
import com.productivity_mangement.productivity.entity.IntegrationAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationAccountRepository
        extends JpaRepository<IntegrationAccount, Long> {

    List<IntegrationAccount> findByUserEmail(String userEmail);

    Optional<IntegrationAccount> findByUserEmailAndProvider(
            String userEmail,
            IntegrationProvider provider
    );
}

