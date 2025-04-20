package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.*;

public class BenchmarksTest {

    @Test
    public void testRunSimulationOutputFormat() {
        String result = Benchmarks.runSimulation(10, 5);
        String[] parts = result.trim().split(",");

        assertEquals(9, parts.length);
        assertDoesNotThrow(() -> Integer.parseInt(parts[0]));
        assertDoesNotThrow(() -> Double.parseDouble(parts[5]));
        assertDoesNotThrow(() -> Double.parseDouble(parts[6]));

        int total = Integer.parseInt(parts[1]);
        int x = Integer.parseInt(parts[2]);
        int o = Integer.parseInt(parts[3]);
        int d = Integer.parseInt(parts[4]);

        assertEquals(total, x + o + d);
    }

    @Test
    public void testRunSimulationFormatAndValues() {
        int iterations = 10; // Use small value to keep test fast
        int games = 5;

        String result = Benchmarks.runSimulation(iterations, games);
        String[] parts = result.trim().split(",");

        // Check column count
        assertEquals(9, parts.length, "Expected 9 columns in CSV output");

        // Check types of a few columns
        assertDoesNotThrow(() -> Integer.parseInt(parts[0]), "Iterations should be an int");
        assertDoesNotThrow(() -> Double.parseDouble(parts[5]), "WinRateX should be a double");
        assertDoesNotThrow(() -> Double.parseDouble(parts[6]), "WinRateO should be a double");

        // Check logical constraints
        int totalGames = Integer.parseInt(parts[1]);
        int xWins = Integer.parseInt(parts[2]);
        int oWins = Integer.parseInt(parts[3]);
        int draws = Integer.parseInt(parts[4]);

        assertEquals(games, totalGames);
        assertEquals(games, xWins + oWins + draws, "Total outcomes must equal total games");

        double avgLength = Double.parseDouble(parts[7]);
        assertTrue(avgLength >= 1 && avgLength <= 9, "Average game length should be between 1 and 9");
    }

    @Test
    public void testSimulationZeroGames() {
        String result = Benchmarks.runSimulation(10, 0);
        String[] parts = result.trim().split(",");
        assertEquals("0", parts[1], "TotalGames should be 0");
    }

    @Test
    public void testSimulationHighIterationsLowGames() {
        String result = Benchmarks.runSimulation(10000, 1);
        String[] parts = result.trim().split(",");
        assertEquals("1", parts[1], "Only one game should be run");
        assertDoesNotThrow(() -> Integer.parseInt(parts[8]), "Execution time should be a number");
    }

    @Test
    public void testRunSimulationBasicOutputFormat() {
        // Given
        int testIterations = 1; // Keep small for test speed
        int games = 2;          // Only 2 games to keep fast

        // When
        String result = Benchmarks.runSimulation(testIterations, games);

        // Then
        // Validate the structure
        String[] parts = result.split(",");
        assertEquals(9, parts.length, "CSV line must contain 11 values");
        assertTrue(result.endsWith("\n"), "CSV line must end with a newline");
    }

    @Test
    public void testMainDoesNotThrow() {
      Benchmarks.GAMES_PER_SETTING=10;
        Benchmarks.runSimulation(2, 1);

        assertDoesNotThrow(() -> Benchmarks.main(new String[]{}));
        java.io.File file = new java.io.File(Benchmarks.CSV_FILE);
        assertTrue(file.exists(), "CSV file should be created");
    }
}