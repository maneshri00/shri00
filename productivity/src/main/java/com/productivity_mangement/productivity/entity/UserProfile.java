package com.productivity_mangement.productivity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String profession;
    private String goal;
    private String wakeTime;
    private String sleepTime;
    private String dob;
    private Integer age;
    @ElementCollection
    @CollectionTable(name = "user_priorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "priority")
    private List<String> priorities;

}

