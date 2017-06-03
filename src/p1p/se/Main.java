package p1p.se;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

  public static void main(String... args)
      throws IOException, URISyntaxException, InterruptedException {
    Path directory = SetupEnvironment.makeItSo(10, 100_000_000);

    Main main = new Main();

    main.calculateWithStreamNotParallell(directory);

    ExecutorService service = Executors.newCachedThreadPool();
    main.calculateWithExecutor(service, directory, "calculateWithCachedThreadPool");
    service.shutdown();

    service = Executors.newFixedThreadPool(10);
    main.calculateWithExecutor(service, directory, "calculateWithFixedThreadPool" + 10);
    service.shutdown();

    service = Executors.newFixedThreadPool(20);
    main.calculateWithExecutor(service, directory, "calculateWithFixedThreadPool" + 20);
    service.shutdown();

    service = Executors.newFixedThreadPool(30);
    main.calculateWithExecutor(service, directory, "calculateWithFixedThreadPool" + 30);
    service.shutdown();

  }

  private void calculateWithStreamNotParallell(Path directory) throws IOException {
    LocalTime time = LocalTime.now();

    Map<Integer, Long> result = flatMapStreamOfStuff(Files.list(directory)
        .filter(p -> p.toString().contains(SetupEnvironment.FILE_NAME))
        .map(this::calculate));
    System.out.println("calculateWithStreamNotParallell Time: " + time.until(LocalTime.now(), ChronoUnit.MILLIS));
    System.out.println("Result: " + result);
  }

  private void calculateWithExecutor(ExecutorService service, Path directory, String type) throws IOException {
    LocalTime time = LocalTime.now();

    List<Callable<Map<Integer, Long>>> tasks = Files.list(directory)
        .filter(p -> p.toString().contains(SetupEnvironment.FILE_NAME))
        .map(s -> (Callable<Map<Integer, Long>>)() -> calculate(s))
        .collect(Collectors.toList());

    Map<Integer, Long> result = new HashMap<>();
    try {
      result = flatMapStreamOfStuff(service.invokeAll(tasks).stream().map(f -> getResult(f)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println(type + " Time: " + time.until(LocalTime.now(), ChronoUnit.MILLIS));
    System.out.println("Result: " + result);

  }

  private Map<Integer, Long> flatMapStreamOfStuff(Stream<Map<Integer, Long>> s){
    return s.map(Map::entrySet)
        .flatMap(Collection::stream)
        .collect(
            Collectors.toMap(
                k -> k.getKey(),
                v -> v.getValue(),
                (s1, s2) -> s1 + s2
            ));
  }

  private Map<Integer, Long> calculate(Path path) {
    Map<Integer, Long> result = null;
    try {
      result = Files.lines(path).map(s -> Integer.parseInt(s))
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
      //System.out.println("Result: " + result);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private Map<Integer, Long> getResult(Future<Map<Integer, Long>> future){
    Map<Integer, Long> r = new HashMap<>();
    try {
      r = future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return r;
  }

}
