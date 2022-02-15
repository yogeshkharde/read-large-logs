package model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "EVENTS")
@Data
public class Event {
    private String id;
    private String state;
    private long timestamp;
    private String type;
    private String host;
    private boolean processed;
    private boolean alert;
}
