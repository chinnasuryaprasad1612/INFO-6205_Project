package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class DotsAndBoxesGameTest {

    @Test
    public void testMain_DefaultArgs() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        DotsAndBoxesGame.main(new String[]{});

        System.setOut(originalOut);
        String out = output.toString();
        assertTrue(out.contains("Starting Dots and Boxes game:"));
        assertTrue(out.contains("Board size: 5x5"));
        assertTrue(out.contains("AI Difficulty: MEDIUM"));
    }

    @Test
    public void testMain_CustomBoardAndDifficulty() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        DotsAndBoxesGame.main(new String[]{"6", "hard"});

        System.setOut(originalOut);
        String out = output.toString();
        assertTrue(out.contains("Board size: 6x6"));
        assertTrue(out.contains("AI Difficulty: HARD"));
    }

}