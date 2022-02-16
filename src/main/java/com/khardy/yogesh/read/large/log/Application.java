package com.khardy.yogesh.read.large.log;

import com.khardy.yogesh.read.large.log.service.LogEventProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
@EnableJpaRepositories
@ComponentScan(basePackages = "com.khardy.yogesh.read.large.log")
public class Application implements CommandLineRunner {

    @Autowired
    private LogEventProcessingService logEventProcessingService;

    public static void main(String[] args) {
        log.info("Starting application");
        if (args.length == 0) {
            log.error("Please provide path of the file to be read");
            System.exit(-1);
        } else if ((!Paths.get(args[0]).toFile().exists())) {
            log.error("Provided path does not exist {}", args[0]);
            System.exit(-1);
        } else {
            SpringApplication.run(Application.class, args);
            log.info("Application finished");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        logEventProcessingService.readFileAndProcessLogEvents(args[0]);
        System.exit(0);
    }
}
