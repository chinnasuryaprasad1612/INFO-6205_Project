package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeStatsTest {

    @Test
    public void testRunSimulationBasicOutputFormat() {
        // Given
        int testIterations = 1; // Keep small for test speed
        int games = 2;          // Only 2 games to keep fast

        // When
        String result = TicTacToeStats.runSimulation(testIterations, games);

        // Then
        // Validate the structure
        String[] parts = result.split(",");
        assertEquals(11, parts.length, "CSV line must contain 11 values");
        assertTrue(result.endsWith("\n"), "CSV line must end with a newline");
    }

    @Test
    public void testMainDoesNotThrow() {
        assertDoesNotThrow(() -> TicTacToeStats.main(new String[]{}));
    }

    @Test
    public void testCSVFileGenerated() throws Exception {
        // Run with smaller input for test
        TicTacToeStats.runSimulation(2, 1);
        // File should exist after main runs
        TicTacToeStats.main(new String[]{});
        java.io.File file = new java.io.File(TicTacToeStats.CSV_FILE);
        assertTrue(file.exists(), "CSV file should be created");
    }
}