package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MCTSTest {

    @Test
    void testSimulateEndsInTerminalState() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        int result = mcts.simulate(root);
        assertTrue(result >= -1 && result <= 1);
    }

    @Test
    void testBackPropagateUpdatesDraw() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        mcts.backPropagate(root, -1); // simulate draw
        assertEquals(1, root.playouts());
        assertEquals(0.5, root.wins());
    }

    @Test
    void testBackPropagateUpdatesWin() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        int opponent = (root.state().player() == TicTacToe.X) ? TicTacToe.O : TicTacToe.X;
        MCTS mcts = new MCTS(root);
        mcts.backPropagate(root, opponent); // simulate a win from root's perspective
        assertEquals(1, root.playouts());
        assertEquals(1.0, root.wins());
    }

    @Test
    void testSelectReturnsRootIfTerminal() {
        // Terminal board (draw)
        String board = "X O X\nX O O\nO X X";
        Position terminalPosition = Position.parsePosition(board, TicTacToe.O);
        TicTacToe.TicTacToeState terminalState = new TicTacToe().new TicTacToeState(terminalPosition);
        TicTacToeNode root = new TicTacToeNode(terminalState);

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
    void testGetBestMoveAfterRun() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        mcts.run(500);
        TicTacToeNode best = mcts.getBestMove();
        assertNotNull(best);
        assertNotEquals(root, best);
    }

    @Test
    void testFindStrategicMovePrefersCenter() {
        // Force an empty board (last move was O, now X to play)
        String board = ". . .\n. . .\n. . .";
        Position position = Position.parsePosition(board, TicTacToe.O);
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState state = game.new TicTacToeState(position);

        MCTS mcts = new MCTS(new TicTacToeNode(state));
        Move<TicTacToe> move = mcts.findStrategicMove(state, List.copyOf(state.moves(state.player())));
        int[] coords = ((TicTacToe.TicTacToeMove) move).move();

        System.out.printf("Player: %d, Chosen Move: (%d, %d)%n", state.player(), coords[0], coords[1]);

        assertEquals(1, coords[0], "Expected move row to be center (1)");
        assertEquals(1, coords[1], "Expected move col to be center (1)");
    }

    @Test
    void testUCTPrefersUnvisited() {
        TicTacToeNode parent = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        TicTacToeNode child = new TicTacToeNode(parent.state().next(
                parent.state().chooseMove(parent.state().player())), parent);
        MCTS mcts = new MCTS(parent);
        double uct = mcts.uct(child);
        assertEquals(Double.POSITIVE_INFINITY, uct);
    }

    @Test
    void testMCTSvsRandomWinsOrDraws() {
        int RUNS = 30;
        int winsOrDraws = 0;
        for (int i = 0; i < RUNS; i++) {
            TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
            MCTS mcts = new MCTS(root);
            while (!root.state().isTerminal()) {
                if (root.state().player() == TicTacToe.X) {
                    mcts.run(300);
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