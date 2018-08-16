package test.mavenexitcode;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import test.mavenexitcode.handler.ErrorDTO;
import test.mavenexitcode.mail.Attachment;
import test.mavenexitcode.mail.MailDTO;

public class Example {

	@Test
	public void errorDtoFromDiskIsResendAndDeleted() throws IOException, InterruptedException {

		ErrorDTO errorDto = getErrorDto();

		new ObjectMapper().writeValue(System.out, errorDto);
	}

	private ErrorDTO getErrorDto() {
		ErrorDTO errorDTO = new ErrorDTO();
		errorDTO.setConsumerRecord("CONSUMER RECORD");
		errorDTO.setErrorTime(ZonedDateTime.now().toString());
		errorDTO.setServiceName(UUID.randomUUID().toString());
		errorDTO.setThrownException(new RuntimeException("MADE A BOO BOO"));
		return errorDTO;
	}

	@Test
	public void maildto() throws JsonGenerationException, JsonMappingException, IOException {
		MailDTO mailDTO = new MailDTO();
		mailDTO.setSubject("SUBJECT");
		mailDTO.setFrom("from@sender.de");
		mailDTO.setTo(Arrays.asList("bar@mail.de", "foo@mail.de"));
		mailDTO.setCc(Arrays.asList("cc@mail.de", "cccc@mail.de"));

		mailDTO.setContent("SOME CONTENT\nSOME MORE");

		Attachment a = new Attachment();
		a.setBinary(false);
		a.setContent("EIN TEXT ATTACHMENT");
		a.setContentType("text/plain");
		a.setName("readme.txt");

		mailDTO.getAttachments().add(a);

		new ObjectMapper().writeValue(System.out, mailDTO);

	}

}
