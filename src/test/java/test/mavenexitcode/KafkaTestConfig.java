package test.mavenexitcode;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import test.mavenexitcode.handler.ErrorDTO;

@Configuration
public class KafkaTestConfig {

	@Value("${kafka.bootstrap.servers:localhost:9092}")
	private String bootstrapServers;

	@Bean
	public Map<String, Object> baseTestConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return props;
	}

	@Bean
	public KafkaTemplate<String, ErrorDTO> kafkaTemplate() {
		return new KafkaTemplate<>(
				new DefaultKafkaProducerFactory<>(
						baseTestConfig(),
						new StringSerializer(),
						new JsonSerializer<>()));
	}
}
