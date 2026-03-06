package com.productivity_mangement.productivity.repository;

import com.productivity_mangement.productivity.DTO.TaskStatus;
import com.productivity_mangement.productivity.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface TaskRepository
        extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByUserEmail(String userEmail);

    boolean existsByTitleAndUserEmail(
            String title,
            String userEmail
    );

    long countByUserEmail(String email);

    long countByUserEmailAndStatus(
            String userEmail,
            TaskStatus status
    );

    List<TaskEntity> findByUserEmailAndStatus(
            String userEmail,
            TaskStatus status
    );
}
