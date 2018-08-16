package test.mavenexitcode.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import test.mavenexitcode.mail.MailDTO;
import test.mavenexitcode.mail.MailSender;

@Component
@EnableRetry
public class MailDAO {

	public static final Logger LOG = LoggerFactory.getLogger(MailDAO.class);

	@Value("${mail.from:noreply@metronom.com}")
	private String from;

	@Value("${mail.to}")
	private String to;

	@Autowired
	private FailureStorage failureStorage;

	@Autowired
	private MailSender mailSender;

	@Retryable(maxAttemptsExpression = "${mail.retries:5}", backoff = @Backoff(delay = 1000, multiplier = 1.5))
	public void sendError(ErrorDTO errorDTO) throws IOException {
		LOG.info("Sending errordto to mail api");
		ResponseEntity<String> response = mailSender.send(createMailDTO(errorDTO));
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("Could not send to mail api");
		}
	}

	@Recover
	public void recover(Exception t, ErrorDTO errorDTO) throws IOException {
		LOG.warn("Could not send mail to rest service: errorDTO={}", errorDTO, t);
		failureStorage.store(errorDTO);
	}

	public boolean sendErrorWithoutRetry(ErrorDTO errorDTO) {
		LOG.info("Sending errordto from storage to mail api");
		try {
			ResponseEntity<String> response = mailSender.send(createMailDTO(errorDTO));

			if (!response.getStatusCode().is2xxSuccessful()) {
				return false;
			}
		} catch (RestClientException | IOException e) {
			LOG.warn("Could not send errordto from storage", e);
			return false;
		}

		return true;
	}

	private MailDTO createMailDTO(ErrorDTO errorDTO) throws IOException {
		MailDTO mailDTO = new MailDTO();
		mailDTO.setSubject(String.format("Error from Service '%s': %s", errorDTO.getServiceName(),
				errorDTO.getThrownException().getMessage()));
		mailDTO.setFrom(from);
		mailDTO.setTo(Arrays.asList(StringUtils.split(to, ",")));

		String stacktrace = getStacktrace(errorDTO);

		String content = String.format(
				"Error from service:%s\n\n"
						+ "Time: %s\n\n"
						+ "Offending Cosumer Record:\n%s\n\n"
						+ "Stacktrace:\n%s\n\n",
				errorDTO.getServiceName(),
				errorDTO.getErrorTime(),
				errorDTO.getConsumerRecord(),
				stacktrace);

		mailDTO.setContent(content);
		return mailDTO;
	}

	private String getStacktrace(ErrorDTO errorDTO) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos)) {
			errorDTO.getThrownException().printStackTrace(ps);
			String stacktrace = baos.toString();
			return stacktrace;
		}
	}

}
