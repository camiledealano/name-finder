package parallel;

import sequential.SequentialNameFinder;
import sequential.SequentialNameFinder.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;

public class ParallelNameFinder {

    public SequentialNameFinder.Result searchFirstOccurrenceParallel(String dirPath, String targetName) throws Exception {
        File dir = new File(dirPath);
        File[] txtFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (txtFiles == null || txtFiles.length == 0) return null;

        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<Result> completionService = new ExecutorCompletionService<>(executor);

        for (File file : txtFiles) {
            completionService.submit(() -> searchInFile(file, targetName));
        }

        try {
            for (int i = 0; i < txtFiles.length; i++) {
                Future<SequentialNameFinder.Result> future = completionService.take();
                SequentialNameFinder.Result result = future.get();
                if (result != null) {
                    executor.shutdownNow();
                    return result;
                }
            }
        } finally {
            executor.shutdown();
        }
        return null;
    }

    private Result searchInFile(File file, String targetName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.equals(targetName)) {
                    return new Result(file.getName(), lineNumber, line);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + file.getName());
        }
        return null;
    }
}