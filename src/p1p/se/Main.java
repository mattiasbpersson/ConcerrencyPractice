package p1p.se;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

  public static void main(String... args) throws IOException, URISyntaxException {
    Path directory = SetupEnvironment.makeItSo(10, 10_000_000);
    LocalTime time = LocalTime.now();

    Files.list(directory)
        .filter(p -> p.toString().contains(SetupEnvironment.FILE_NAME))
        .forEach(Main::calculate);
    System.out.println("Time: " + time.until(LocalTime.now(), ChronoUnit.MILLIS));
  }

  private static Map<Integer, Long> calculate(Path path) {
    Map<Integer, Long> result = null;
    try {
      result = Files.lines(path).map(s -> Integer.parseInt(s))
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      System.out.println("Result: " + result);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}
