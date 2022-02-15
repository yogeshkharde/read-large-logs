package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import executor.LogEventEvaluationTask;
import model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.EventsRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

@Service
public class LogEventProcessingService {
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private final EventsRepository eventsRepository;

    @Autowired
    public LogEventProcessingService(ObjectMapper objectMapper,
                                     ExecutorService executorService,
                                     EventsRepository eventsRepository) {
        this.objectMapper = objectMapper;
        this.executorService = executorService;
        this.eventsRepository = eventsRepository;
    }

    public void readFile(String filePath) throws IOException {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream, "UTF-8");
            List<String> lines = new ArrayList<>();
            List<LogEventEvaluationTask> logEventEvaluationTasks = new ArrayList<>();
            while (sc.hasNextLine()) {
                String eventLine = sc.nextLine();
                Event event = objectMapper.readValue(eventLine, Event.class);
                eventsRepository.save(event);
                // System.out.println(line);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
            for (int i = 0; i < 7; i++) {
                LogEventEvaluationTask logEventEvaluationTask = new LogEventEvaluationTask(eventsRepository);
                logEventEvaluationTasks.add(logEventEvaluationTask);
            }
            executorService.invokeAll(logEventEvaluationTasks);
        } catch (InterruptedException e) {
            e.printStackTrace(); //todo add logs
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
