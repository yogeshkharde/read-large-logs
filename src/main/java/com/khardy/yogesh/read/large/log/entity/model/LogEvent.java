package com.khardy.yogesh.read.large.log.entity.model;

import lombok.Data;

@Data
public class LogEvent {

    private String id;
    private String state;
    private long timestamp;
    private String type;
    private String host;
}
