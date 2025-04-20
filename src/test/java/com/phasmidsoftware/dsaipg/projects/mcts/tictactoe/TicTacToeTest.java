package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeTest {

    @Test
    public void testOpener() {
        TicTacToe game = new TicTacToe();
        assertEquals(TicTacToe.X, game.opener());
    }

    @Test
    public void testStartReturnsValidState() {
        TicTacToe game = new TicTacToe();
        State<TicTacToe> state = game.start();
        assertNotNull(state);
        assertFalse(state.isTerminal());
    }

    @Test
    public void testRunGameDeterministic() {
        TicTacToe game = new TicTacToe(0L); // Seeded for determinism
        State<TicTacToe> finalState = game.runGame();
        Optional<Integer> winner = finalState.winner();
        assertTrue(winner.isPresent() || finalState.isTerminal());
    }

    @Test
    public void testRunGameWithIterations() {
        TicTacToe game = new TicTacToe(42L);
        State<TicTacToe> finalState = game.runGame(100);
        assertNotNull(finalState);
        assertTrue(finalState.isTerminal());
    }

    @Test
    public void testConstructorWithRandom() {
        Random random = new Random(42);
        TicTacToe game = new TicTacToe(random);
        assertNotNull(game);
    }

    @Test
    public void testConstructorWithSeed() {
        TicTacToe game = new TicTacToe(123L);
        assertNotNull(game);
    }

    @Test
    public void testDefaultConstructor() {
        TicTacToe game = new TicTacToe();
        assertNotNull(game);
    }

    @Test
    public void testTicTacToeMoveFields() {
        TicTacToe.TicTacToeMove move = new TicTacToe.TicTacToeMove(TicTacToe.X, 1, 2);
        assertEquals(TicTacToe.X, move.player());
        assertArrayEquals(new int[]{1, 2}, move.move());
    }

    @Test
    public void testTicTacToeStateMethods() {
        TicTacToe game = new TicTacToe(123L);
        TicTacToe.TicTacToeState state = game.new TicTacToeState();

        assertEquals(game, state.game());
        assertEquals(TicTacToe.X, state.player()); // X always starts
        assertFalse(state.isTerminal());
        assertTrue(state.moves(TicTacToe.X).size() == 9);

        Move<TicTacToe> move = state.chooseMove(TicTacToe.X);
        State<TicTacToe> nextState = state.next(move);
        assertNotNull(nextState);
        assertTrue(nextState instanceof TicTacToe.TicTacToeState);
    }
    @Test
    public void testTicTacToeStateToString() {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState state = game.new TicTacToeState();
        String stateStr = state.toString();
        assertTrue(stateStr.contains("TicTacToe"));
    }

    @Test
    public void testTicTacToeStateConstructorWithStartingPlayer() {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState state = game.new TicTacToeState(TicTacToe.X);
        assertNotNull(state);
    }
    @Test
    public void testGameMethod() {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState state = game.new TicTacToeState();
        assertEquals(game, state.game());
    }
    @Test
    public void testSimulateGameOutput() {
        String output = TicTacToe.simulateGameOutput();
        assertNotNull(output);
        assertTrue(output.contains("TicTacToe:"));
    }

}
