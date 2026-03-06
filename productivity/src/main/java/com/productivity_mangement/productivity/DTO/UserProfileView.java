package com.productivity_mangement.productivity.DTO;

import lombok.Data;

@Data
public class UserProfileView {

    private String name;
    private Integer age;
    private String dob;
    private String wakeTime;
    private String sleepTime;
    private Long completedTaskCount;
    private long totalTaskCount;
}
