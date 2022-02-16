package com.khardy.yogesh.read.large.log.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EVENTS")
@Getter
@Setter
public class Event {
    @EmbeddedId
    private EventId eventId;
    private long timestamp;
    private String type;
    private String host;
    private boolean processed;

    public Event(){

    }

    public Event(String id, String state, long timestamp, String type, String host) {
        this.eventId = new EventId(id, state);
        this.timestamp = timestamp;
        this.host = host;
        this.type = type;
    }
}
