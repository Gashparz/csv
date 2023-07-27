package epredescu.csvqueueproducer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "q.domains";

    @Bean
    public List<Queue> createUserRegistrationQueues() {
        int numReplicas = getNumReplicas();
        List<Queue> queues = new ArrayList<>();

        IntStream.range(0, numReplicas).forEach(i -> {
            String queueName = QUEUE_NAME + i;
            Queue queue = new Queue(queueName);
            queues.add(queue);
        });

        return queues;
    }

    public static int getNumReplicas() {
        String numReplicasStr = System.getenv("NUM_REPLICAS");
        if (numReplicasStr != null) {
            return Integer.parseInt(numReplicasStr);
        } else {
            return 1;
        }
    }
}
