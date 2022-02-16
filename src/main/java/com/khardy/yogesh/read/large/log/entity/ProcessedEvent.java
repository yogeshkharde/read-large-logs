package com.khardy.yogesh.read.large.log.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROCESSED_EVENTS")
@Getter
@Setter
public class ProcessedEvent {
    @Id
    private String id;
    private long duration;
    private String type;
    private String host;
    private boolean alert;
}
