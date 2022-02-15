package repository;

import model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventsRepository extends CrudRepository<Event, String> {
    List<Event> findTop500ByNotProcessed();
}
