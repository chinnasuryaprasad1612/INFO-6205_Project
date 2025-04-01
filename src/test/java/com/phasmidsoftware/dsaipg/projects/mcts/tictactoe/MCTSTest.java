package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class MCTSTest {

    @Test
    void testSimulateEndsInTerminalState() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        int result = mcts.simulate(root, root.state().player());
        assertTrue(result >= -1 && result <= 1);
    }

    @Test
    void testBackPropagateUpdatesStats() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        int result = mcts.simulate(root, root.state().player());
        mcts.backPropagate(root, result);
        assertTrue(root.playouts() > 0);
    }

    @Test
    void testSelectReturnsRootIfNoChildren() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        Node<TicTacToe> selected = mcts.select(root);
        assertEquals(root, selected);
    }

    @Test
    void testRunExpandsChildren() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        mcts.run(100);
        assertFalse(root.children().isEmpty());
    }

    @Test
    void testMCTSvsRandomWinsOrDraws() {
        int RUNS = 50;
        int winsOrDraws = 0;
        for (int i = 0; i < RUNS; i++) {
            TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
            MCTS mcts = new MCTS(root);
            while (!root.state().isTerminal()) {
                if (root.state().player() == TicTacToe.X) {
                    mcts.run(200);
                    Node<TicTacToe> best = root.children().stream()
                            .max((a, b) -> Integer.compare(a.playouts(), b.playouts()))
                            .orElseThrow();
                    root = new TicTacToeNode(best.state());
                } else {
                    Move<TicTacToe> move = root.state().chooseMove(TicTacToe.O);
                    root = new TicTacToeNode(root.state().next(move));
                }
                mcts = new MCTS(root);
            }
            int winner = root.state().winner().orElse(-1);
            if (winner == -1 || winner == TicTacToe.X) winsOrDraws++;
        }
        assertTrue(winsOrDraws >= RUNS * 0.5);
    }


}
