package parallel;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ParallelFileSplitFinder {
    public static class Result {
        public final String fileName;
        public final int lineNumber;
        public final String lineContent;

        public Result(String fileName, int lineNumber, String lineContent) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.lineContent = lineContent;
        }

        @Override
        public String toString() {
            return String.format("%s:%d - %s", fileName, lineNumber, lineContent);
        }
    }

    public List<Result> searchInFiles(String dirPath, String targetName) throws Exception {
        List<Result> allResults = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (Stream<Path> paths = Files.walk(Paths.get(dirPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(filePath -> {
                        executor.submit(() -> {
                            try {
                                List<String> lines = Files.readAllLines(filePath);
                                for (int i = 0; i < lines.size(); i++) {
                                    if (lines.get(i).trim().equalsIgnoreCase(targetName)) {
                                        allResults.add(new Result(
                                                filePath.getFileName().toString(),
                                                i + 1,
                                                lines.get(i)
                                        ));
                                    }
                                }
                            } catch (IOException e) {
                                System.err.println("Error processing file: " + filePath);
                            }
                        });
                    });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        return allResults;
    }
}