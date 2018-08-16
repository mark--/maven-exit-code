package test.mavenexitcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(scanBasePackages = { "iam.metro.cloud.*" })
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@EnableRetry
public class Main implements ApplicationRunner {

	public static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		LOG.info("ErrorQueue handler started");
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}
}
