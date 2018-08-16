package test.mavenexitcode.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import test.mavenexitcode.handler.FailureStorage.FailedErrorDTO;

@Component
@EnableScheduling
public class RetryScheduler {
	public static final Logger LOG = LoggerFactory.getLogger(RetryScheduler.class);

	@Autowired
	private FailureStorage failureStorage;

	@Autowired
	private MailDAO mailRestDAO;

	@Scheduled(cron = "${resend.cron:0 0 1 * * *}")
	public void tryResend() throws IOException {
		LOG.info("Trying to resend errordtos to mail api");

		failureStorage
				.failedDTOs()
				.filter(e -> mailRestDAO.sendErrorWithoutRetry(e.getErrorDTO()))
				.forEach(FailedErrorDTO::removeFromStorage);
	}

}
