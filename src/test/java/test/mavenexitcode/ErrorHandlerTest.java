package test.mavenexitcode;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import test.mavenexitcode.handler.ErrorDTO;
import test.mavenexitcode.handler.ErrorTopicHandler;
import test.mavenexitcode.handler.MailDAO;
import test.mavenexitcode.mail.MailSender;

//@RunWith(SpringRunner.class) // must have
//@EmbeddedKafka(count = 1, topics = { "errorQueue" })
//@SpringBootTest(properties = {
//		"kafka.bootstrap.servers=${spring.embedded.kafka.brokers}",
//})
public class ErrorHandlerTest {

	@Autowired
	private KafkaTemplate<String, ErrorDTO> kafkaTemplate;

	@Autowired
	private ErrorTopicHandler cut;

	@MockBean
	private MailDAO dao;

	@MockBean
	private MailSender rtf;

	@Test
	public void a() throws InterruptedException, IOException {

		// kafkaTemplate.send("errorQueue", new ErrorDTO(new RuntimeException("dds"),
		// "record"));
		//
		// Thread.sleep(2000);
		// verify(dao).sendError(Mockito.any());

	}

}
