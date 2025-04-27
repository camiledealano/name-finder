package parallel;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ParallelBoyerMooreFinder {
    private static class BoyerMoore {
        private final int[] right;
        private final String pattern;
        private final boolean caseInsensitive;

        public BoyerMoore(String pattern, boolean caseInsensitive) {
            this.caseInsensitive = caseInsensitive;
            this.pattern = caseInsensitive ? pattern.toLowerCase() : pattern;
            int m = pattern.length();
            int R = 256;
            right = new int[R];

            Arrays.fill(right, -1);
            for (int j = 0; j < m; j++) {
                char c = caseInsensitive ? Character.toLowerCase(pattern.charAt(j)) : pattern.charAt(j);
                right[c] = j;
            }
        }

        public boolean search(String text) {
            int m = pattern.length();
            int n = text.length();

            if (caseInsensitive) {
                text = text.toLowerCase();
            }

            int skip;
            for (int i = 0; i <= n - m; i += skip) {
                skip = 0;
                for (int j = m-1; j >= 0; j--) {
                    char textChar = text.charAt(i+j);
                    char patternChar = pattern.charAt(j);

                    if (textChar != patternChar) {
                        skip = Math.max(1, j - right[textChar]);
                        break;
                    }
                }
                if (skip == 0) return true;
            }
            return false;
        }
    }

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

    public List<Result> searchInDirectory(String dirPath, String targetName, boolean exactMatch) throws Exception {
        List<Result> allResults = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        BoyerMoore bm = new BoyerMoore(targetName, true);

        try (Stream<Path> paths = Files.walk(Paths.get(dirPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(filePath -> {
                        executor.submit(() -> {
                            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                                String line;
                                int lineNumber = 0;
                                while ((line = reader.readLine()) != null) {
                                    lineNumber++;
                                    line = line.trim();
                                    if (exactMatch) {
                                        if (bm.search(line) && line.length() == targetName.length()) {
                                            synchronized (allResults) {
                                                allResults.add(new Result(
                                                        filePath.getFileName().toString(),
                                                        lineNumber,
                                                        line
                                                ));
                                            }
                                        }
                                    } else {
                                        if (bm.search(line)) {
                                            synchronized (allResults) {
                                                allResults.add(new Result(
                                                        filePath.getFileName().toString(),
                                                        lineNumber,
                                                        line
                                                ));
                                            }
                                        }
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