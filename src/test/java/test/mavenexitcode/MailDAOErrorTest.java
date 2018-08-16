package test.mavenexitcode;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Optional;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import test.mavenexitcode.handler.ErrorDTO;
import test.mavenexitcode.handler.FailureStorage;
import test.mavenexitcode.handler.MailDAO;
import test.mavenexitcode.mail.MailSender;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"mail.to=test@metro.de",
		"mail.retries=2",
		"failure.folder=target/failures",
})
@ContextConfiguration(classes = { MailDAO.class, FailureStorage.class })
public class MailDAOErrorTest {

	@Value("${failure.folder}")
	private String failureFolder;

	@SpyBean
	private MailDAO cut;

	@MockBean
	private MailSender ms;

	@Test
	public void sendingMailisRetried() throws IOException {
		when(ms.send(Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		cut.sendError(getErrorDto());
		verify(cut, times(2)).sendError(Mockito.any());
	}

	@Test
	public void errorDtoIsStoredOnDisk() throws IOException {
		when(ms.send(Mockito.any())).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
		Path f = Paths.get(failureFolder);

		ErrorDTO errorDto = getErrorDto();
		cut.sendError(errorDto);

		Optional<Path> filesAfter = fileInFailureFolder(f, errorDto.getServiceName());

		try (InputStream in = Files.newInputStream(filesAfter.get())) {
			ErrorDTO errorDtoFromFile = new ObjectMapper().readValue(in, ErrorDTO.class);
			assertThat(errorDtoFromFile.getServiceName(), equalTo(errorDto.getServiceName()));
		}
	}

	private Optional<Path> fileInFailureFolder(Path f, String id) throws IOException {
		return Files
				.list(f.resolve(id))
				.filter(Files::isRegularFile)
				.findFirst();
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
