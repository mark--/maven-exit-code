package test.mavenexitcode.handler;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@EnableKafka
@Configuration
public class ErrorTopicHandler {
	public static final Logger LOG = LoggerFactory.getLogger(ErrorTopicHandler.class);

	@Autowired
	private MailDAO mailRestDAO;

	@KafkaListener(groupId = "${errorgroup:errorgroup}", topics = "${errorqueue:errorQueue}", containerFactory = "errorTopicKafkaListenerContainerFactory")
	public void handleError(@Payload Optional<ErrorDTO> error) throws Exception {

		if (error.isPresent()) {
			LOG.info("Received message");
			mailRestDAO.sendError(error.get());
		} else {
			LOG.info("Received empty message");
		}
	}
}
