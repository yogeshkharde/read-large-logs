package executor;

import lombok.AllArgsConstructor;
import model.Event;
import repository.EventsRepository;

import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class LogEventPersistTask implements Callable<Integer> {

    private EventsRepository eventsRepository;

    private List<Event> events;


    @Override
    public Integer call() throws Exception {
        this.eventsRepository.saveAll(this.events);
        return this.events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
