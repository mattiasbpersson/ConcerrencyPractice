package p1p.se;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

	public static void main(String... args) throws IOException, URISyntaxException {
		Path directory = SetupEnvironment.makeItSo(10, 100_000);
		Files.list(directory).filter(p -> p.toString().contains(SetupEnvironment.FILE_NAME)).forEach(
                    (path) -> {
                      try {
                        calculate(path);
                      } catch (IOException e) {
                        e.printStackTrace();
                      } catch (URISyntaxException e) {
                        e.printStackTrace();
                      }
                    });
	}

	private static void calculate(Path path) throws IOException, URISyntaxException {
		//ClassLoader classLoader = getClass().getClassLoader();
		//Path path = Paths.get(classLoader.getResource("numbers2.txt").toURI());
		LocalTime time = LocalTime.now();
		Map<Integer, Long> result = Files.lines(path).map(s -> Integer.parseInt(s))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		System.out.println("Time: " + time.until(LocalTime.now(), ChronoUnit.MILLIS));
		System.out.println("Result: " + result);
	}
}
