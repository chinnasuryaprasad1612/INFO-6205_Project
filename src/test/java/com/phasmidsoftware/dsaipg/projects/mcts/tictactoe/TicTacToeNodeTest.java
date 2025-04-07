package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicTacToeNodeTest {

    @Test
    public void winsAndPlayouts() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState(
                Position.parsePosition("X . O\nX O .\nX . O", TicTacToe.X));
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.isLeaf());
        int result = state.winner().orElse(-1);
        new MCTS(node).backPropagate(node, result);
        assertEquals(2.0, node.wins(), 0.001);
        assertEquals(1, node.playouts());
    }
    @Test
    public void state() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(state, node.state());
    }

    @Test
    public void white() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.white());
    }

    @Test
    public void children() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.children().isEmpty()); // Initially no children

        State<TicTacToe> childState = state.next(state.chooseMove(state.player()));
        node.addChild(childState);

        assertEquals(1, node.children().size()); // One child added
        assertTrue(node.children().iterator().next() instanceof TicTacToeNode);
    }


    @Test
    public void addChild() {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState state = game.new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);

        Move<TicTacToe> move = state.chooseMove(state.player());
        State<TicTacToe> newState = state.next(move);

        node.addChild(newState);

        assertEquals(1, node.children().size());
        Node<TicTacToe> child = node.children().iterator().next();
        TicTacToe.TicTacToeState expected = (TicTacToe.TicTacToeState) newState;
        TicTacToe.TicTacToeState actual = (TicTacToe.TicTacToeState) child.state();

        assertEquals(expected.position().toString(), actual.position().toString());

    }

    @Test
    public void backPropagate() {
        TicTacToe.TicTacToeState state = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(state);
        node.backPropagate();
        assertEquals(node.playouts(), 0);
        assertEquals(node.wins(), 0.0,0.0);
        assertEquals(node.children().size(), 0);
        assertEquals(node.playouts(), 0);
        assertEquals(node.wins(), 0.0,0);
    }



}