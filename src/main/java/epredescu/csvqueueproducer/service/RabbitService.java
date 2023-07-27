package epredescu.csvqueueproducer.service;

import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static epredescu.csvqueueproducer.config.RabbitMQConfig.QUEUE_NAME;
import static epredescu.csvqueueproducer.config.RabbitMQConfig.getNumReplicas;

@Service
public class RabbitService {
    public void sendMessages() throws IOException {
        produceMessages(readCsv());
    }

    private static List<String> readCsv() throws IOException {
        Resource resource = new ClassPathResource("sample-websites.csv");
        InputStream inputStream = resource.getInputStream();
        List<String> domains = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                domains.add(line);
            }
        }
        return domains;
    }

    private static void produceMessages(List<String> domains) {
        int numReplicas = getNumReplicas();
        List<List<String>> partitions = Lists.partition(domains, (domains.size() + numReplicas - 1) / numReplicas);
        IntStream.range(0, numReplicas)
                .forEach(i -> {
                    List<String> messagesChunk = partitions.get(i);
                    sendMessages(i, messagesChunk);
                });
    }

    private static void sendMessages(int replicaNumber, List<String> domains) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME + replicaNumber, false, false, false, null);
            for (String domain : domains) {
                channel.basicPublish("", QUEUE_NAME + replicaNumber, null, domain.getBytes());
                System.out.println("Sent message: " + domain + " to " + QUEUE_NAME + replicaNumber);
            }
        } catch (TimeoutException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
