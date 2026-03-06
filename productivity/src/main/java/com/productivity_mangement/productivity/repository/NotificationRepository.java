package com.productivity_mangement.productivity.repository;

import com.productivity_mangement.productivity.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    long countByUserEmailAndReadIsFalse(String userEmail);
}

