package com.productivity_mangement.productivity.repository;

import com.productivity_mangement.productivity.entity.SlackToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackTokenRepository extends JpaRepository<SlackToken, Long> {
}
