package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.*;

public class BenchmarksTest {

    private static final String TMP_PATH = "src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/dotsandboxes/dotsandboxes_benchmarking_results.csv";
    private Benchmarks benchmarks;

    @Before
    public void setUp() {
        // Assume file already exists, so no writing/creating
        benchmarks = new Benchmarks(5, TMP_PATH);
    }

    @Test
    public void testRunExperimentSetAppendsToCSV() throws Exception {
        benchmarks.runExperimentSet(3, 10); // test logic only

        File file = new File(TMP_PATH);
        assertTrue("CSV file should exist", file.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            assertNotNull("CSV should have a header", header);
            assertTrue("Header should contain 'Iterations'", header.contains("Iterations"));

            String data = reader.readLine();
            assertNotNull("Should have at least one row of data", data);
            assertEquals("Data should have 9 columns", 9, data.split(",").length);
        }
    }

    @Test
    public void testRunIterationExperimentsAppendsMultipleLines() throws Exception {
        benchmarks = new Benchmarks(10);
        benchmarks.runIterationExperiments(); // multiple sets

        File file = new File(TMP_PATH);
        assertTrue("CSV file should exist", file.exists());

        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) lineCount++;
        }

        assertTrue("CSV should contain header + multiple rows", lineCount > 1);
    }
}