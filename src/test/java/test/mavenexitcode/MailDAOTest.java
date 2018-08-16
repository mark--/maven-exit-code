package test.mavenexitcode;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import test.mavenexitcode.handler.ErrorDTO;
import test.mavenexitcode.handler.FailureStorage;
import test.mavenexitcode.handler.MailDAO;
import test.mavenexitcode.mail.MailSender;

@RunWith(SpringRunner.class) // must have
@SpringBootTest(properties = {
		"mail.rest.url=http://localhost:43567/restmail",
		"mail.to=test@metro.de",
		"mail.rest.user=fakefoo",
		"mail.rest.password=foobar",
})
@ContextConfiguration(classes = { MailDAO.class, MailSender.class })
public class MailDAOTest {

	@Autowired
	private MailDAO cut;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(43567);

	@MockBean
	FailureStorage fs;

	@Test
	public void mailIsSentForIncomingErroDTO() throws IOException {

		stubFor(post(urlEqualTo("/restmail")).willReturn(aResponse().withStatus(200)));

		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setConsumerRecord("CONSUMER RECORD");
		errorDTO.setErrorTime("NOW NOW NOW");
		errorDTO.setServiceName("SERVICE");
		errorDTO.setThrownException(new RuntimeException("MADE A BOO BOO"));

		cut.sendError(errorDTO);
		verify(postRequestedFor(urlEqualTo("/restmail")).withHeader("Authorization", matching(".*")));
	}

}
