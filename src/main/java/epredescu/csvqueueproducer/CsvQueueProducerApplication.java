package epredescu.csvqueueproducer;

import epredescu.csvqueueproducer.service.RabbitService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsvQueueProducerApplication {
    private static RabbitService rabbitService = null;

    public CsvQueueProducerApplication(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CsvQueueProducerApplication.class, args);
        rabbitService.sendMessages();
    }



}
