import parallel.ParallelNameFinder;
import sequential.SequentialNameFinder;

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

            System.out.println("\n-------- SPEEDUP E SLOWDOWN --------");

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
}