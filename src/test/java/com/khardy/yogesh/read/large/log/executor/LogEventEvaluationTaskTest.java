package com.khardy.yogesh.read.large.log.executor;

import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.EventId;
import com.khardy.yogesh.read.large.log.entity.ProcessedEvent;
import com.khardy.yogesh.read.large.log.entity.model.State;
import com.khardy.yogesh.read.large.log.repository.EventsRepository;
import com.khardy.yogesh.read.large.log.repository.ProcessedEventsRepository;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogEventEvaluationTaskTest {

    @Mock
    private ProcessedEventsRepository processedEventsRepository;

    @Mock
    private EventsRepository eventsRepository;

    @InjectMocks
    private LogEventEvaluationTask logEventEvaluationTask;

    @Captor
    private ArgumentCaptor<List<ProcessedEvent>> processedEventArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<EventId>> eventArgumentCaptor;

    @Test
    public void testEvaluation() {
        //setup
        Event event1 = createEvent(State.STARTED, "1", "type", "host", 1l);
        Event event2 = createEvent(State.FINISHED, "1", "type", "host", 6l);
        Event event3 = createEvent(State.STARTED, "2", "type", "host", 6l);
        Event event4 = createEvent(State.FINISHED, "2", "type", "host", 9l);
        Event event5 = createEvent(State.STARTED, "3", "type", "host", 6l);
        when(eventsRepository.findTop5000ByProcessed(false)).thenReturn(Lists.newArrayList(event1, event2, event3, event4, event5));
        doNothing().when(eventsRepository).deleteAllById(Mockito.anyList());
        when(processedEventsRepository.saveAll(Mockito.anyList())).thenReturn(Lists.newArrayList());

        //execute
        Integer call = logEventEvaluationTask.call();

        //assert
        Mockito.verify(processedEventsRepository).saveAll(processedEventArgumentCaptor.capture());
        Mockito.verify(eventsRepository).deleteAllById(eventArgumentCaptor.capture());
        Assert.assertEquals(2, call.intValue());

        List<ProcessedEvent> allValues = processedEventArgumentCaptor.getValue();
        Assert.assertEquals(2, allValues.size());
        Assert.assertEquals("1", allValues.stream().filter(ProcessedEvent::isAlert).findFirst().get().getId());
        Assert.assertEquals("2", allValues.stream().filter(value -> !value.isAlert()).findFirst().get().getId());
        Set<String> allValues1 = eventArgumentCaptor.getValue().stream().map(EventId::getId).collect(Collectors.toSet());
        Assert.assertEquals(2, allValues1.size());
        Assert.assertTrue(allValues1.contains("1"));
        Assert.assertTrue(allValues1.contains("2"));
    }

    private Event createEvent(State state, String id, String type, String host, long timestamp) {
        EventId eventId = new EventId();
        eventId.setId(id);
        eventId.setState(state.name());

        Event event = new Event();
        event.setEventId(eventId);
        event.setProcessed(false);
        event.setHost(host);
        event.setType(type);
        event.setTimestamp(timestamp);

        return event;
    }

}