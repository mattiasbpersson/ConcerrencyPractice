package p1p.se;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SetupEnvironment {

  private static final String DATA_DIR = "data";
  public static final String FILE_NAME = "numbers";

  public static Path makeItSo(int numberOfFiles, int numbersPerFile)
      throws IOException, InterruptedException {
    Path directory = createDirectory();
    ExecutorService service = Executors.newFixedThreadPool(numberOfFiles);
    IntStream.range(1, numberOfFiles)
        .forEach(n -> service.submit(() -> generateRandom(Paths.get(DATA_DIR, FILE_NAME + n + ".txt"), numbersPerFile)));
    service.shutdown();
    System.out.println("Waiting for files to be generated");
    while(!service.isTerminated()) {
      System.out.print(".");
      service.awaitTermination(1, TimeUnit.SECONDS);
    }
    System.out.println("Files created");

    return directory;
  }

  private static Path createDirectory() throws IOException {
    Path directory = Paths.get(DATA_DIR);
    if (Files.notExists(directory)) {
      Files.createDirectory(directory);
    }
    return directory;
  }

  private static void generateRandom(Path path, int size) {
    try {
      if (Files.exists(path)) {
        System.out.println("File exists: " + path.toString());
        if (Files.lines(path).count() != size) {
          System.out.println("Files has wrong size, will be deleted");
          Files.delete(path);
        } else {
          return;
        }
      }

      System.out.println("Creating " + path.toString());
      Files.createFile(path);
      BufferedWriter writer = Files.newBufferedWriter(path);
      Random rand = new Random();
      rand.ints(size).map(n -> Math.abs(n) % 10).forEach(i -> {
        try {
          String out = Integer.toString(i);
          writer.append(out);
          writer.newLine();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
