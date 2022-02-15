package executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Event;
import repository.EventsRepository;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class LogEventParserTask implements Runnable {

    private ObjectMapper objectMapper;

    private List<String> eventLines;

    private EventsRepository eventsRepository;

    @Override
    public void run() {
        List<Event> events = new ArrayList<>();
        this.eventLines.forEach(line -> {
            try {
                events.add(this.objectMapper.readValue(line, Event.class));
            } catch (JsonProcessingException e) {
                log.warn("Could not create event from log line {}, Moving to next line", line, e);
            }
        });
        this.eventsRepository.saveAll(events);
    }

    public void setEventLines(List<String> eventLines) {
        this.eventLines = eventLines;
    }
}
