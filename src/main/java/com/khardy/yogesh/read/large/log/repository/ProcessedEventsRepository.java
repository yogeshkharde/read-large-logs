package com.khardy.yogesh.read.large.log.repository;

import com.khardy.yogesh.read.large.log.entity.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventsRepository extends CrudRepository<ProcessedEvent, String> {
}
