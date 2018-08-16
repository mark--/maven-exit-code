package test.mavenexitcode.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FailureStorage {

	public static final Logger LOG = LoggerFactory.getLogger(FailureStorage.class);

	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

	@Value("${failure.folder:}")
	private String failureFolder;

	public void store(ErrorDTO errorDTO) throws IOException {

		if (StringUtils.isBlank(failureFolder)) {
			LOG.warn("Not storing errordto on disk due to missing configuration");
			return;
		}

		Path path = pathForErrorDTO(errorDTO);
		LOG.info("Writing errordto to disk: path={}", path);

		try (OutputStream out = Files.newOutputStream(path)) {
			new ObjectMapper().writeValue(out, errorDTO);
		} catch (IOException e) {
			LOG.error("Could not write errordto to file", e);
		}
	}

	public Stream<FailedErrorDTO> failedDTOs() throws IOException {
		Files.createDirectories(Paths.get(failureFolder));
		return Files.list(Paths.get(failureFolder))
				.filter(Files::isDirectory)
				.flatMap(this::list)
				.filter(Files::isRegularFile)
				.map(f -> new FailedErrorDTO(read(f), () -> remove(f)))
				.filter(f -> f.getErrorDTO() != null);
	}

	void remove(Path path) {
		try {
			// LOG.info().message("Deleting error dto from filesystem").field("path",
			// path).log();
			Files.deleteIfExists(path);
		} catch (IOException e) {
			// LOG.error().exception("Could not delete failure dto", e).field("path",
			// path).log();
		}
	}

	private Stream<Path> list(Path path) {
		try {
			return Files.list(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ErrorDTO read(Path path) {
		try (InputStream in = Files.newInputStream(path)) {
			return new ObjectMapper().readValue(in, ErrorDTO.class);
		} catch (IOException e) {
			LOG.warn("Could not read errordto from file: path={}", path);
			return null;
		}
	}

	private Path pathForErrorDTO(ErrorDTO errorDTO) throws IOException {
		Path result = Paths.get(
				failureFolder,
				StringUtils.defaultIfEmpty(errorDTO.getServiceName(), "NOSERVICE"),
				ZonedDateTime.now().format(DF) + ".json");

		Files.createDirectories(result.getParent());

		return result;
	}

	static class FailedErrorDTO {
		private ErrorDTO errorDTO;
		private Runnable removeFromStorage;

		public FailedErrorDTO(ErrorDTO errorDTO, Runnable removeFromStorage) {
			this.errorDTO = errorDTO;
			this.removeFromStorage = removeFromStorage;
		}

		public ErrorDTO getErrorDTO() {
			return errorDTO;
		}

		public void removeFromStorage() {
			removeFromStorage.run();
		}
	}
}
