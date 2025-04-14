package sequential;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SequentialNameFinder {

    public Result searchFirstOccurrence(String directoryPath, String targetName) {
        try (Stream<Path> paths = Files.list(Paths.get(directoryPath))) {
            return paths
                    .filter(path -> path.toString().toLowerCase().endsWith(".txt"))
                    .filter(Files::isRegularFile)
                    .map(path -> searchInFile(path.toFile(), targetName))
                    .filter(result -> result != null)
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            System.err.println("[Erro] Falha ao ler diretório: " + directoryPath);
            return null;
        }
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
            System.err.println("[Erro] Falha ao ler arquivo: " + file.getName());
        }
        return null;
    }

    public static class Result {
        private final String fileName;
        private final int lineNumber;
        private final String lineContent;

        public Result(String fileName, int lineNumber, String lineContent) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.lineContent = lineContent;
        }

        @Override
        public String toString() {
            return String.format(
                    "Arquivo: %s | Linha: %d | Conteúdo: \"%s\"",
                    fileName, lineNumber, lineContent
            );
        }
    }
}