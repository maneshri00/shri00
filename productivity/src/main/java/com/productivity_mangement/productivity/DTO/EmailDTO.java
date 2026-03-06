package com.productivity_mangement.productivity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class EmailDTO {
    private String id;
    private String from;
    private String subject;
    private String snippet;
    private long timestamp;
    private List<String> labels;


}

