package com.khardy.yogesh.read.large.log.repository;

import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.EventId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventsRepository extends CrudRepository<Event, EventId> {
    List<Event> findTop500ByProcessed(boolean isProcessed);

    long countByProcessed(boolean isProcessed);
}
