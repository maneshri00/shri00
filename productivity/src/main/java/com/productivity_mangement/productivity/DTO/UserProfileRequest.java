package com.productivity_mangement.productivity.DTO;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.List;

@Data
public class UserProfileRequest {

    private String name;
    private String profession;
    private String goal;
    private String wakeTime;
    private String sleepTime;
    private List<String> priorities;
    private Integer age;
    private String dob;

}
