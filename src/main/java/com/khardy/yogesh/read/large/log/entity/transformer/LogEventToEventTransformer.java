package com.khardy.yogesh.read.large.log.entity.transformer;

import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.model.LogEvent;
import org.springframework.stereotype.Component;

/**
 * Class transforming the log event to the event to be inserted in the database
 */
@Component
public class LogEventToEventTransformer {

    public Event transformLogEventToEvent(LogEvent logEvent){
        return new Event(logEvent.getId(), logEvent.getState(), logEvent.getTimestamp(), logEvent.getType(), logEvent.getHost());
    }

}
