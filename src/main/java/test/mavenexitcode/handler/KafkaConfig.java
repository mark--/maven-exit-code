package test.mavenexitcode.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class KafkaConfig {

	@Value("${kafka.bootstrap.servers:localhost:9092}")
	private String bootstrapServers;

	@Bean
	public Map<String, Object> baseConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return props;
	}

	@Bean
	ConsumerFactory<String, Optional<ErrorDTO>> errorTopicConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(
				baseConfig(),
				new StringDeserializer(),
				new JsonDeserializer<>(ErrorDTO.class));
	}

	@Bean("errorTopicKafkaListenerContainerFactory")
	KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Optional<ErrorDTO>>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Optional<ErrorDTO>> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(errorTopicConsumerFactory());
		factory.setConcurrency(3);
		factory.getContainerProperties().setPollTimeout(3000);

		return factory;
	}

}
