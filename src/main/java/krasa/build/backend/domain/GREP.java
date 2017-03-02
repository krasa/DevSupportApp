package krasa.build.backend.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class GREP {

	public static void main(String[] args) throws IOException {
		Stream<String> duration = grep("duration", "C:\\logy\\usage-access-api.pfwqde4ep.qde4ep.20161123.log");
		duration.forEach(s -> System.out.println());

	}

	public static Stream<String> grep(String pattern, String fileName) throws IOException {
		return Files.lines(Paths.get(fileName)).filter(line -> line.matches(pattern));
	}
}
