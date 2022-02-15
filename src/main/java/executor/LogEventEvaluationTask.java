package executor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Event;
import repository.EventsRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.groupingBy;

@AllArgsConstructor
@Slf4j
public class LogEventEvaluationTask implements Callable<Integer> {

    private EventsRepository eventsRepository;

    @Override
    public Integer call() {
        List<Event> top500ByNotProcessed = eventsRepository.findTop500ByNotProcessed();
        Map<String, List<Event>> eventsGroupedByEventId = top500ByNotProcessed.stream().collect(groupingBy(Event::getId));
        eventsGroupedByEventId
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 2)
                .forEach(entry -> {
                    List<Event> events = entry.getValue();
                    AtomicLong startTime = new AtomicLong(-1L);
                    AtomicLong finishedTime = new AtomicLong(-1L);

                    events.forEach(event -> {

                        if (event.getState().equalsIgnoreCase("FINISHED")) {
                            finishedTime.set(event.getTimestamp());
                        } else {
                            startTime.set(event.getTimestamp());
                        }
                        event.setProcessed(true);
                    });
                    if (finishedTime.get() - startTime.get() > 4) {
                        log.info("Event with id {} took longer than 4 ms", entry.getKey());
                    }
                });
        eventsRepository.saveAll(top500ByNotProcessed);
        return 0;
    }
}
