package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class SimulatorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testSimulationRunCompletes() {
        DotsAndBoxesGame game = new DotsAndBoxesGame(3); // Small board
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();
        Simulator simulator = new Simulator(state, DotsAndBoxesMcts.Difficulty.EASY);

        simulator.run();

        assertTrue(outContent.toString().contains("Game Over!"));
        assertTrue(simulator.getMoveCount() > 0);
    }

    @Test
    public void testIncrementMoveCount() {
        DotsAndBoxesGame game = new DotsAndBoxesGame(3);
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();
        Simulator simulator = new Simulator(state);

        int before = simulator.getMoveCount();
        simulator.incrementMoveCount();
        assertEquals(before + 1, simulator.getMoveCount());
    }

    @Test
    public void testGetCurrentState() {
        DotsAndBoxesGame game = new DotsAndBoxesGame(3);
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();
        Simulator simulator = new Simulator(state);

        assertEquals(state, simulator.getCurrentState());
    }

    @Test
    public void testDisplayAndWinnerOutput() {
        DotsAndBoxesGame game = new DotsAndBoxesGame(3);
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();
        Simulator simulator = new Simulator(state);

        simulator.displayBoard();
        simulator.announceWinner();

        String output = outContent.toString();
        assertTrue(output.contains("Player 1") || output.contains("Player 2") || output.contains("It's a tie!"));
    }
}
