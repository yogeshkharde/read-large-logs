package com.khardy.yogesh.read.large.log.entity.transformer;

import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.model.LogEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogEventToEventTransformerTest {

    @Test
    public void testTransformation() {

        //setup
        LogEvent logEvent = mock(LogEvent.class);
        when(logEvent.getId()).thenReturn("1");
        when(logEvent.getHost()).thenReturn("host");
        when(logEvent.getState()).thenReturn("state");
        when(logEvent.getTimestamp()).thenReturn(1l);
        when(logEvent.getType()).thenReturn("type");
        LogEventToEventTransformer transformer = new LogEventToEventTransformer();

        //execute
        Event event = transformer.transformLogEventToEvent(logEvent);

        //verify
        Assert.assertEquals(event.getEventId().getId(), "1");
        Assert.assertEquals(event.getEventId().getState(), "state");
        Assert.assertEquals(event.getHost(), "host");
        Assert.assertEquals(event.getTimestamp(), 1L);
        Assert.assertEquals(event.getType(), "type");

    }

}