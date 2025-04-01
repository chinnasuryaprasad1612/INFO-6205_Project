package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class TicTacToeStats {

    public static final int GAMES_PER_SETTING = 50;
    public static final int[] ITERATION_COUNTS = {50, 100, 200, 400, 800, 1600, 3200, 6400,12800,25600,51200};
    public static final String CSV_FILE = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/tictactoe_metrics.csv";

    public static void main(String[] args) throws IOException {
        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            writer.write("Iterations,TotalGames,XWins,OWins,Draws,WinRateX,WinRateO,AvgGameLength,TotalSimulations,AvgSimPerGame,TimeMillis\n");

            for (int iterations : ITERATION_COUNTS) {
                String line = runSimulation(iterations, GAMES_PER_SETTING);
                writer.write(line);
            }

            System.out.println("✅ Metrics written to: " + CSV_FILE);
        }
    }

    public static String runSimulation(int iterations, int gamesPerSetting) {
        int xWins = 0, oWins = 0, draws = 0, totalMoves = 0;
        long totalSimulations = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < gamesPerSetting; i++) {
            TicTacToe game = new TicTacToe();
            TicTacToeNode root = new TicTacToeNode(game.new TicTacToeState());
            int moves = 0;

            while (!root.state().isTerminal()) {
                MCTS mcts = new MCTS(root);
                mcts.run(iterations);
                root = mcts.getBestMove();
                root = new TicTacToeNode(root.state());
                moves++;
                totalSimulations += iterations;
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

        long timeMillis = System.currentTimeMillis() - startTime;
        double winRateX = (xWins * 100.0) / gamesPerSetting;
        double winRateO = (oWins * 100.0) / gamesPerSetting;
        double avgGameLength = totalMoves * 1.0 / gamesPerSetting;
        double avgSimPerGame = totalSimulations * 1.0 / gamesPerSetting;

        return String.format(Locale.US,
                "%d,%d,%d,%d,%d,%.2f,%.2f,%.2f,%d,%.2f,%d\n",
                iterations, gamesPerSetting, xWins, oWins, draws,
                winRateX, winRateO, avgGameLength, totalSimulations, avgSimPerGame, timeMillis);
    }
}