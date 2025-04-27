import parallel.ParallelNameFinder;
import sequential.SequentialNameFinder;
import parallel.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String targetName = readName(scanner);
            String directorySmall = "src/data/small";
            String directoryLarge = "src/data/large";

            System.out.println("\n-------- BUSCA SEQUENCIAL --------");
            double timeSeqSmall = searchInDirectorySequential(directorySmall, targetName);
            double timeSeqLarge = searchInDirectorySequential(directoryLarge, targetName);

            System.out.println("\n-------- BUSCA PARALELA --------");
            double timeParSmall = searchInDirectoryParallel(directorySmall, targetName);
            double timeParLarge = searchInDirectoryParallel(directoryLarge, targetName);

            System.out.println("\n-------- BOYER-MOORE PARALELO --------");
            double timeBmSmall = searchWithBoyerMoore(directorySmall, targetName);
            double timeBmLarge = searchWithBoyerMoore(directoryLarge, targetName);

            System.out.println("\n-------- FILE SPLIT PARALELO --------");
            double timeFsSmall = searchWithFileSplit(directorySmall, targetName);
            double timeFsLarge = searchWithFileSplit(directoryLarge, targetName);

            System.out.println("\n-------- SPEEDUP/SLOWDOWN --------");

            if (timeParSmall < timeSeqSmall) {
                double speedupSmall = timeSeqSmall / timeParSmall;
                System.out.printf("Speedup (pequeno): %.2f (A busca paralela foi %.2f vezes mais rápida que a sequencial no diretório pequeno.)\n", speedupSmall, speedupSmall);
            } else if (timeSeqSmall < timeParSmall) {
                double slowdownSmall = timeParSmall / timeSeqSmall;
                System.out.printf("Slowdown (pequeno): %.2f (A busca sequencial foi %.2f vezes mais rápida que a paralela no diretório pequeno.)\n", slowdownSmall, slowdownSmall);
            }

            if (timeParLarge < timeSeqLarge) {
                double speedupLarge = timeSeqLarge / timeParLarge;
                System.out.printf("Speedup (grande): %.2f (A busca paralela foi %.2f vezes mais rápida que a sequencial no diretório grande.)\n", speedupLarge, speedupLarge);
            } else if (timeSeqLarge < timeParLarge) {
                double slowdownLarge = timeParLarge / timeSeqLarge;
                System.out.printf("Slowdown (grande): %.2f (A busca sequencial foi %.2f vezes mais rápida que a paralela no diretório grande.)\n", slowdownLarge, slowdownLarge);
            }

            System.out.println("\n-------- SPEEDUP/SLOWDOWN BOYER-MOORE --------");
            if (timeBmSmall < timeSeqSmall) {
                double speedupBmSmall = timeSeqSmall / timeBmSmall;
                System.out.printf("Speedup BM (pequeno): %.2f (Boyer-Moore foi %.2f× mais rápido que sequencial)\n", speedupBmSmall, speedupBmSmall);
            } else if (timeSeqSmall < timeBmSmall) {
                double slowdownBmSmall = timeBmSmall / timeSeqSmall;
                System.out.printf("Slowdown BM (pequeno): %.2f (Sequencial foi %.2f× mais rápido que Boyer-Moore)\n", slowdownBmSmall, slowdownBmSmall);
            }

            if (timeBmLarge < timeSeqLarge) {
                double speedupBmLarge = timeSeqLarge / timeBmLarge;
                System.out.printf("Speedup BM (grande): %.2f (Boyer-Moore foi %.2f× mais rápido que sequencial)\n", speedupBmLarge, speedupBmLarge);
            } else if (timeSeqLarge < timeBmLarge) {
                double slowdownBmLarge = timeBmLarge / timeSeqLarge;
                System.out.printf("Slowdown BM (grande): %.2f (Sequencial foi %.2f× mais rápido que Boyer-Moore)\n", slowdownBmLarge, slowdownBmLarge);
            }

            System.out.println("\n-------- SPEEDUP/SLOWDOWN FILE SPLIT --------");
            if (timeFsSmall < timeSeqSmall) {
                double speedupFsSmall = timeSeqSmall / timeFsSmall;
                System.out.printf("Speedup FS (pequeno): %.2f (File Split foi %.2f× mais rápido que sequencial)\n", speedupFsSmall, speedupFsSmall);
            } else if (timeSeqSmall < timeFsSmall) {
                double slowdownFsSmall = timeFsSmall / timeSeqSmall;
                System.out.printf("Slowdown FS (pequeno): %.2f (Sequencial foi %.2f× mais rápido que File Split)\n", slowdownFsSmall, slowdownFsSmall);
            }

            if (timeFsLarge < timeSeqLarge) {
                double speedupFsLarge = timeSeqLarge / timeFsLarge;
                System.out.printf("Speedup FS (grande): %.2f (File Split foi %.2f× mais rápido que sequencial)\n", speedupFsLarge, speedupFsLarge);
            } else if (timeSeqLarge < timeFsLarge) {
                double slowdownFsLarge = timeFsLarge / timeSeqLarge;
                System.out.printf("Slowdown FS (grande): %.2f (Sequencial foi %.2f× mais rápido que File Split)\n", slowdownFsLarge, slowdownFsLarge);
            }

            System.out.print("----------------------------------------");
            System.out.print("\nDeseja buscar outro nome? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();
            if (!resposta.equals("s")) break;
        }
    }

    private static String readName(Scanner scanner) {
        System.out.print("Digite o nome a ser buscado: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.err.println("Erro: Nome não pode ser vazio.");
            System.exit(1);
        }
        return name;
    }

    private static double searchInDirectorySequential(String directoryPath, String targetName) {
        SequentialNameFinder finder = new SequentialNameFinder();
        long startTime = System.nanoTime();
        SequentialNameFinder.Result result = finder.searchFirstOccurrence(directoryPath, targetName);
        double time = (System.nanoTime() - startTime) / 1_000_000_000.0;

        System.out.println("\nDiretório: " + directoryPath);
        System.out.println(result != null ? result : "Nome não encontrado.");
        System.out.printf("Tempo (sequencial): %.6f s\n", time);
        return time;
    }

    private static double searchInDirectoryParallel(String directoryPath, String targetName) throws Exception {
        ParallelNameFinder finder = new ParallelNameFinder();
        long startTime = System.nanoTime();
        SequentialNameFinder.Result result = finder.searchFirstOccurrenceParallel(directoryPath, targetName);
        double time = (System.nanoTime() - startTime) / 1_000_000_000.0;

        System.out.println("\nDiretório: " + directoryPath);
        System.out.println(result != null ? result : "Nome não encontrado.");
        System.out.printf("Tempo (paralelo): %.6f s\n", time);
        return time;
    }

    private static double searchWithBoyerMoore(String directoryPath, String targetName) throws Exception {
        ParallelBoyerMooreFinder finder = new ParallelBoyerMooreFinder();
        long startTime = System.nanoTime();
        List<ParallelBoyerMooreFinder.Result> results = finder.searchInDirectory(directoryPath, targetName, true);
        double time = (System.nanoTime() - startTime) / 1_000_000_000.0;

        System.out.println("\nDiretório: " + directoryPath);
        System.out.println("Resultados encontrados: " + results.size());
        if (!results.isEmpty()) {
            System.out.println("Primeiro resultado: " + results.get(0));
        }
        System.out.printf("Tempo (Boyer-Moore): %.6f s\n", time);
        return time;
    }

    private static double searchWithFileSplit(String directoryPath, String targetName) throws Exception {
        ParallelFileSplitFinder finder = new ParallelFileSplitFinder();
        long startTime = System.nanoTime();
        List<ParallelFileSplitFinder.Result> results = finder.searchInFiles(directoryPath, targetName);
        double time = (System.nanoTime() - startTime) / 1_000_000_000.0;

        System.out.println("\nDiretório: " + directoryPath);
        System.out.println("Resultados encontrados: " + results.size());
        if (!results.isEmpty()) {
            System.out.println("Primeiro resultado: " + results.get(0));
        }
        System.out.printf("Tempo (File Split): %.6f s\n", time);
        return time;
    }
}