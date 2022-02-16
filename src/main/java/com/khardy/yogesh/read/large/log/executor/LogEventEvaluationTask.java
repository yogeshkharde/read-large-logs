package com.khardy.yogesh.read.large.log.executor;

import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.ProcessedEvent;
import com.khardy.yogesh.read.large.log.entity.model.State;
import com.khardy.yogesh.read.large.log.repository.EventsRepository;
import com.khardy.yogesh.read.large.log.repository.ProcessedEventsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Task evaluating the log events and making entry in the processed events table
 */
@AllArgsConstructor
@Slf4j
public class LogEventEvaluationTask implements Callable<Integer> {

    private ProcessedEventsRepository processedEventsRepository;
    private EventsRepository eventsRepository;

    @Override
    public Integer call() {
        List<Event> events = eventsRepository.findTop500ByProcessed(false); // get all unprocessed events

        List<ProcessedEvent> processedEvents = new ArrayList<>();

        events
                .stream()
                .collect(groupingBy(event -> event.getEventId().getId())) // collect data in a map of event id to events
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 2) // filter only those events which have both start and finish
                .forEach(entry -> {
                    List<Event> eventsBeingProcessed = entry.getValue();
                    ProcessedEvent processedEvent = new ProcessedEvent(); // create a processed event entry
                    processedEvent.setId(entry.getKey());
                    processedEvent.setHost(eventsBeingProcessed.get(0).getHost());
                    processedEvent.setType(eventsBeingProcessed.get(0).getType());
                    AtomicLong startTime = new AtomicLong(-1L);
                    AtomicLong finishedTime = new AtomicLong(-1L);

                    eventsBeingProcessed.forEach(event -> {
                        if (event.getEventId().getState().equalsIgnoreCase(State.FINISHED.name())) {
                            finishedTime.set(event.getTimestamp());
                        } else {
                            startTime.set(event.getTimestamp());
                        }
                        event.setProcessed(true); // set the event as processed
                    });
                    long duration = finishedTime.get() - startTime.get();
                    processedEvent.setDuration(duration);

                    if (duration > 4) {
                        log.info("Event with id {} took longer than 4 ms", entry.getKey());
                        processedEvent.setAlert(true); // if the duration is more than 4 ms the set the alert as true
                    }
                    processedEvents.add(processedEvent);
                });
        processedEventsRepository.saveAll(processedEvents); // save the processed entries
        eventsRepository.deleteAllById(events.stream().filter(Event::isProcessed).map(Event::getEventId).collect(Collectors.toList()));//delete the processed entries from EVENTS table
        return processedEvents.size();
    }
}
