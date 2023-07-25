package epredescu.csvqueueproducer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static epredescu.csvqueueproducer.CsvQueueProducerApplication.QUEUE_NAME;


@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue createUserRegistrationQueue() {
        return new Queue(QUEUE_NAME);
    }
}
