package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages AI vs AI experiments and records outcomes with iteration-based benchmarking.
 */
public class Benchmarks {

    private final String filename;
    private boolean headerWritten = false;
    private final int gamesPerSetting;

    public Benchmarks(int gamesPerSetting) {
        this.gamesPerSetting = gamesPerSetting;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        this.filename = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/dotsandboxes/dotsandboxes_benchmarking_results.csv";

        try (FileWriter writer = new FileWriter(filename, false)) {
            writer.write("Iterations,TotalGames,P1Wins,P2Wins,Draws,WinRateP1,WinRateP2,AvgGameLength,TimeMillis\n");
            headerWritten = true;
        } catch (IOException e) {
            System.err.println("❌ Failed to initialize CSV file: " + e.getMessage());
        }
    }

    public Benchmarks(int gamesPerSetting, String filename) {
        this.gamesPerSetting = gamesPerSetting;
        this.filename = filename;

        try (FileWriter writer = new FileWriter(filename, false)) {
            writer.write("Iterations,TotalGames,P1Wins,P2Wins,Draws,WinRateP1,WinRateP2,AvgGameLength,TimeMillis\n");
            headerWritten = true;
        } catch (IOException e) {
            System.err.println("❌ Failed to initialize CSV file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        //change the number of games
        Benchmarks manager = new Benchmarks(1000);
        manager.runIterationExperiments();
    }

    public void runIterationExperiments() {

        int[] experimentSet = {100,200,400,800,1600,3200,6400,12800,25600,51200};
        for (int j : experimentSet) {
            runExperimentSet(5, j);
        }

    }

    public void runExperimentSet(int boardSize, int iterations) {
        System.out.println("\n=============================================");
        System.out.printf("Running %d AI vs AI games on %dx%d board\n", gamesPerSetting, boardSize, boardSize);
        System.out.printf("Players: %d iterations\n", iterations);
        System.out.println("=============================================");

        int p1Wins = 0, p2Wins = 0, draws = 0;
        long totalMoves = 0;

        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(iterations, Math.sqrt(2));
        long totalStartTime = System.currentTimeMillis(); // Start total time here

        for (int i = 1; i <= gamesPerSetting; i++) {
            System.out.println("\n▶ Game " + i);

            DotsAndBoxesGame game = new DotsAndBoxesGame(boardSize);
           // DotsAndBoxesState state = (DotsAndBoxesState) game.start();
            int startingPlayer = (i % 2 == 0) ? 1 : 2;
            DotsAndBoxesState state = new DotsAndBoxesState(game, startingPlayer);

            int moves = 0;
            while (!state.isTerminal()) {
                Move<DotsAndBoxesGame> move = mcts.findBestMove(state);
                if (move == null) break;
                state = (DotsAndBoxesState) state.next(move);
                moves++;
            }

            totalMoves += moves;

            int[] scores = state.getScores();
            int winner = scores[0] > scores[1] ? 1 : (scores[1] > scores[0] ? 2 : 0);

            switch (winner) {
                case 1 -> p1Wins++;
                case 2 -> p2Wins++;
                default -> draws++;
            }

            System.out.printf("Game %d: Winner = Player %d, Score = %d-%d, Moves = %d\n",
                    i, winner, scores[0], scores[1], moves);
        }

        long totalExecutionTime = System.currentTimeMillis() - totalStartTime; // End total time here

        double winRateP1 = (double) p1Wins / gamesPerSetting * 100;
        double winRateP2 = (double) p2Wins / gamesPerSetting * 100;
        double avgGameLength = (double) totalMoves / gamesPerSetting;

        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(String.format("%d,%d,%d,%d,%d,%.2f,%.2f,%.2f,%d\n",
                    iterations, gamesPerSetting, p1Wins, p2Wins, draws,
                    winRateP1, winRateP2, avgGameLength, totalExecutionTime));
        } catch (IOException e) {
            System.err.println("❌ Failed to write to CSV: " + e.getMessage());
        }

        System.out.printf("\n📊 Summary for %dx%d board (%d iterations):\n",
                boardSize, boardSize, iterations);
        System.out.printf("Games: %d, P1 Wins: %d (%.2f%%), P2 Wins: %d (%.2f%%), Draws: %d\n",
                gamesPerSetting, p1Wins, winRateP1, p2Wins, winRateP2, draws);
        System.out.printf("Avg Game Length: %.2f moves, Total Execution Time: %dms\n",
                avgGameLength, totalExecutionTime);
        System.out.printf("Results saved to: %s\n", filename);
    }
}
