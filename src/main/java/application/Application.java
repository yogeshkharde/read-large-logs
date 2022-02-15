package application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        log.info("Starting application");
        if(args.length ==0 ){
            log.error("Please provide path of the file to be read");
            System.exit(-1);
        }
        SpringApplication.run(Application.class, args);
        log.info("application.Application finished");
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
