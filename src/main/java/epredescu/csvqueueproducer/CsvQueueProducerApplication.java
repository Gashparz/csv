package epredescu.csvqueueproducer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class CsvQueueProducerApplication {
	public static final String QUEUE_NAME = "q.domains";

	public static void main(String[] args) throws IOException {
		SpringApplication.run(CsvQueueProducerApplication.class, args);
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("rabbitmq");
		try (Connection connection = factory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);

			Resource resource = new ClassPathResource("sample-websites.csv");
			InputStream inputStream = resource.getInputStream();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = br.readLine()) != null) {
					channel.basicPublish("", QUEUE_NAME, null, line.getBytes());
					System.out.println("Sent URL: " + line);
				}
			}
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
}
