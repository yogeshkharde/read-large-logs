package com.khardy.yogesh.read.large.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khardy.yogesh.read.large.log.entity.Event;
import com.khardy.yogesh.read.large.log.entity.model.LogEvent;
import com.khardy.yogesh.read.large.log.entity.transformer.LogEventToEventTransformer;
import com.khardy.yogesh.read.large.log.executor.LogEventEvaluationTask;
import com.khardy.yogesh.read.large.log.repository.EventsRepository;
import com.khardy.yogesh.read.large.log.repository.ProcessedEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * Service class to process log events , put into in memory hsqldb and send for evaluations
 */
@Service
@Slf4j
public class LogEventProcessingService {

    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private final ProcessedEventsRepository processedEventsRepository;
    private final EventsRepository eventsRepository;
    private final LogEventToEventTransformer logEventToEventTransformer;

    @Autowired
    public LogEventProcessingService(ObjectMapper objectMapper,
                                     ExecutorService executorService,
                                     ProcessedEventsRepository processedEventsRepository,
                                     EventsRepository eventsRepository,
                                     LogEventToEventTransformer logEventToEventTransformer) {
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.processedEventsRepository = processedEventsRepository;
        this.eventsRepository = eventsRepository;
        this.logEventToEventTransformer = logEventToEventTransformer;
    }

    /**
     * @param filePath path of the file with events
     * @throws IOException if there is issue in reading the file
     */
    public void readFileAndProcessLogEvents(String filePath) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            List<Event> events = new ArrayList<>();
            while (sc.hasNextLine()) {
                String eventLine = sc.nextLine();
                LogEvent logEvent = objectMapper.readValue(eventLine, LogEvent.class);
                events.add(logEventToEventTransformer.transformLogEventToEvent(logEvent));
                if (events.size() == 10000) {
                    eventsRepository.saveAll(events); // save 10000 events each in the EVENTS table
                    events.clear();
                }
            }

            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }

            List<LogEventEvaluationTask> evaluationTasks = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                evaluationTasks.add(new LogEventEvaluationTask(processedEventsRepository, eventsRepository)); // create tasks for evaluating log events
            }

            // run the evaluation tasks until all events are processed
            while (eventsRepository.countByProcessed(false) > 0) {
                executorService.invokeAll(evaluationTasks);
            }

            log.info("Total events processed {}",processedEventsRepository.count());
            log.info("Total events to be alerted {}",processedEventsRepository.countByAlert(true));
        } catch (InterruptedException e) {
            log.warn("Thread interrupted", e);
        } catch (IOException ioe) {
            log.warn("Exception during reading log file", ioe);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
