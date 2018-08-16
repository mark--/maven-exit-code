package test.mavenexitcode;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import test.mavenexitcode.handler.ErrorDTO;
import test.mavenexitcode.handler.FailureStorage;
import test.mavenexitcode.handler.MailDAO;
import test.mavenexitcode.handler.RetryScheduler;
import test.mavenexitcode.mail.MailSender;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"mail.to=test@metro.de",
		"mail.retries=2",
		"resend.cron=* * * * * *",
		"failure.folder=target/failures",
})
@ContextConfiguration(classes = { MailDAO.class, FailureStorage.class, RetryScheduler.class })
public class MailRestDAOResendTest {

	@Value("${failure.folder}")
	private String failureFolder;

	@SpyBean
	private MailDAO cut;

	@MockBean
	private MailSender ms;

	@SpyBean
	private FailureStorage failureStorage;

	@Test
	public void errorDtoFromDiskIsResendAndDeleted() throws IOException, InterruptedException {
		FileSystemUtils.deleteRecursively(new File(failureFolder));
		when(ms.send(Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

		ErrorDTO errorDto = getErrorDto();
		cut.sendError(errorDto);
		Thread.sleep(1000);
		verify(cut, atLeastOnce()).sendErrorWithoutRetry(Mockito.any());
		when(ms.send(Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());
		Thread.sleep(2000);
		verify(failureStorage).remove(Mockito.any());
	}

	private ErrorDTO getErrorDto() {
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setConsumerRecord("CONSUMER RECORD");
		errorDTO.setErrorTime(ZonedDateTime.now().toString());
		errorDTO.setServiceName(UUID.randomUUID().toString());
		errorDTO.setThrownException(new RuntimeException("MADE A BOO BOO"));
		return errorDTO;
	}

}
