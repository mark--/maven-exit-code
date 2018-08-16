package test.mavenexitcode.mail;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MailSender {

	@Value("${mail.rest.url}")
	private String mailEndpoint;

	@Value("${mail.rest.user:}")
	private String mailUser;

	@Value("${mail.rest.password:}")
	private String mailPassword;

	private URI mailUrl;

	@PostConstruct
	public void init() throws URISyntaxException {
		mailUrl = new URI(mailEndpoint);
	}

	@Bean
	private RestTemplate restTemplate() throws URISyntaxException {
		if (StringUtils.isBlank(mailUser)) {
			return new RestTemplate();
		} else {
			URI uri = new URI(mailEndpoint);
			HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
			RestTemplate restTemplate = new RestTemplate(new BasicAuthRequestFactory(host));
			restTemplate.getInterceptors().add(
					new BasicAuthorizationInterceptor(mailUser, mailPassword));

			return restTemplate;
		}
	}

	public ResponseEntity<String> send(MailDTO mailDTO) {
		try {
			return restTemplate()
					.postForEntity(mailUrl, mailDTO, String.class);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e); // should not happen
		}
	}
}
