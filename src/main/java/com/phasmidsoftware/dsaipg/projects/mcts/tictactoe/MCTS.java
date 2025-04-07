// ✅ Final MCTS.java using direct setWins/setPlayouts backpropagation logic

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class MCTS {

    private Node<TicTacToe> root;

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public Node<TicTacToe> getRoot() {
        return root;
    }

    public void run(int iterations) {
        int currentPlayer = root.state().player();
        for (int i = 0; i < iterations; i++) {
            Node<TicTacToe> node = select(root);
            int result = simulate(node, currentPlayer);
            backPropagate(node, result);
        }
    }

    Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf()) {
            if (!node.children().isEmpty()) {
                node = getBestChild(node);
            } else {
                node.explore();
                return node;
            }
        }
        return node;
    }

    Node<TicTacToe> getBestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparingDouble(this::uct))
                .orElseThrow(() -> new IllegalStateException("No best child found"));
    }

    private double uct(Node<TicTacToe> node) {
        if (node.playouts() == 0) return Double.POSITIVE_INFINITY;
        double c = Math.sqrt(2);
        Node<TicTacToe> parent = node.getParent();
        int parentPlayouts = (parent != null) ? parent.playouts() : 1; // avoid log(0)
        return (node.wins() / (double) node.playouts()) +
                c * Math.sqrt(Math.log(parentPlayouts) / (double) node.playouts());
    }

    int simulate(Node<TicTacToe> node, int targetPlayer) {
        State<TicTacToe> state = node.state();
        while (!state.isTerminal()) {
            List<Move<TicTacToe>> moves = new ArrayList<>(state.moves(state.player()));
            Move<TicTacToe> best = findHeuristicMove(state, moves);
            state = state.next(best);
        }
        return state.winner().orElse(-1);
    }

    Move<TicTacToe> findHeuristicMove(State<TicTacToe> state, List<Move<TicTacToe>> moves) {
        // 1. Winning move?
        for (Move<TicTacToe> move : moves) {
            if (state.next(move).winner().orElse(-1) == state.player()) return move;
        }

        // 2. Block opponent?
        int opponent = (state.player() == TicTacToe.X) ? TicTacToe.O : TicTacToe.X;
        for (Move<TicTacToe> move : moves) {
            if (state.next(move).winner().orElse(-1) == opponent) return move;
        }

        // 3. Prefer center (1,1)
        for (Move<TicTacToe> move : moves) {
            int[] coords = ((TicTacToe.TicTacToeMove) move).move();
            if (coords[0] == 1 && coords[1] == 1) return move;
        }

        // 4. Otherwise random
        return moves.get(state.random().nextInt(moves.size()));
    }

    void backPropagate(Node<TicTacToe> node, int result) {
        while (node != null) {
            node.setPlayouts(node.playouts() + 1);

            Node<TicTacToe> parent = node.getParent();
            int movePlayer;

            if (parent == null) {
               movePlayer = (node.state().player() == TicTacToe.X) ? TicTacToe.O : TicTacToe.X;
            } else {
                movePlayer = parent.state().player();
            }

            if (result == -1) {
                node.setWins(node.wins() + 1); // draw = 1 point
            } else if (movePlayer == result) {
                node.setWins(node.wins() + 2); // win = 2 points
            }

            node = parent;
        }
    }
    public TicTacToeNode getBestMove() {
        return (TicTacToeNode) root.children().stream()
                .max(Comparator.comparing(Node::playouts))
                .orElseThrow(() -> new IllegalStateException("No children to choose from. Run MCTS first."));
    }
}
