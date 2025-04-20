package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Benchmarks {

    //change the number of games
    public static  int GAMES_PER_SETTING = 1000;
    public static final int[] ITERATION_COUNTS = {100,200,400,800,1600,3200,6400,12800,25600,51200};
    public static final String CSV_FILE = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/tictactoe_benchmarking_results.csv";



    public static void main(String[] args) throws IOException, InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>());
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            writer.write("Iterations,TotalGames,XWins,OWins,Draws,WinRateX,WinRateO,AvgGameLength,totalExecutionTimeMillis\n");

            List<Supplier<String>> simulationSupplierList = new ArrayList<>();
            for (int iterations : ITERATION_COUNTS) {
                simulationSupplierList.add(() -> runSimulation(iterations, GAMES_PER_SETTING));
            }

            List<String> csv = supplyAllAsync(simulationSupplierList, executor);

            for (String x : csv) {
                writer.write(x);
            }
            System.out.println("Metrics written to: " + CSV_FILE);
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                System.out.println("Executor didn't terminate in time. Forced shutdown.");
            }
        }
    }

    public static <T> List<T> supplyAllAsync(final List<Supplier<T>> suppliers, final ExecutorService executorService) {
        List<CompletableFuture<T>> futures = suppliers.stream()
                .map(supplier -> CompletableFuture.supplyAsync(supplier, executorService))
                .toList();
        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } catch (CompletionException e) {
            throw new RuntimeException(e.getCause().getMessage(), e);
        }
    }

    public static String runSimulation(int iterations, int gamesPerSetting) {
        int xWins = 0, oWins = 0, draws = 0, totalMoves = 0;

        long startTime = System.currentTimeMillis();

        System.out.println("Running simulation with iterations: " + iterations + " on " + Thread.currentThread().getName());
        for (int i = 0; i < gamesPerSetting; i++) {
            TicTacToe game = new TicTacToe();
            int startingPlayer = (i % 2 == 0) ? TicTacToe.X : TicTacToe.O;
            TicTacToeNode root = new TicTacToeNode(game.new TicTacToeState(startingPlayer));
            int moves = 0;

            while (!root.state().isTerminal()) {
                MCTS mcts = new MCTS(root);
                mcts.run(iterations);
                root = mcts.getBestMove();
                root = new TicTacToeNode(root.state());
                moves++;
            }

            Optional<Integer> winner = root.state().winner();
            if (winner.isEmpty()) {
                draws++;
            } else if (winner.get() == TicTacToe.X) {
                xWins++;
            } else {
                oWins++;
            }

            totalMoves += moves;
        }

        long totalExecutionTimeMillis = System.currentTimeMillis() - startTime;
        double winRateX = (xWins * 100.0) / gamesPerSetting;
        double winRateO = (oWins * 100.0) / gamesPerSetting;
        double avgGameLength = totalMoves * 1.0 / gamesPerSetting;

        System.out.println("Successfully simulation completed of iterations: " + iterations + " on " + Thread.currentThread().getName());
        return String.format(Locale.US,
                "%d,%d,%d,%d,%d,%.2f,%.2f,%.2f,%d\n",
                iterations, gamesPerSetting, xWins, oWins, draws,
                winRateX, winRateO, avgGameLength, totalExecutionTimeMillis);
    }
}