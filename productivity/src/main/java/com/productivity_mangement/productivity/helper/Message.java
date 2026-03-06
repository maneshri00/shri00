package com.productivity_mangement.productivity.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String from;
    private String content;
    private String subject;
    private List<String> files;
}
