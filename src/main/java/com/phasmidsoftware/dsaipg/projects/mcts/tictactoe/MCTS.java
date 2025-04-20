
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class MCTS {

    private final Node<TicTacToe> root;
    private final double explorationParameter; // Tunable exploration parameter

    public MCTS(Node<TicTacToe> root) {
        this(root, 1.414); // Default to sqrt(2)
    }

    public MCTS(Node<TicTacToe> root, double explorationParameter) {
        this.root = root;
        this.explorationParameter = explorationParameter;
    }

    public Node<TicTacToe> getRoot() {
        return root;
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            // 1. Selection + Expansion: Select a promising node to expand
            Node<TicTacToe> selected = select(root);

            // 2. Simulation: Simulate random playout from selected node
            int simulatedResult = simulate(selected);

            // 3. Backpropagation: Update statistics based on simulation result
            backPropagate(selected, simulatedResult);
        }
    }

    Node<TicTacToe> select(Node<TicTacToe> node) {
        // Keep going down the tree until we reach a leaf node
        while (!node.isLeaf()) {
            // If all children have been explored, choose best child according to UCT
            if (node.children() != null && !node.children().isEmpty()) {
                node = getBestChild(node);
            } else {
                // If node has unexplored moves, expand one of them
                node.explore();
                return node;
            }
        }

        // If we reach a terminal node, return it
        if (node.state().isTerminal()) {
            return node;
        }

        // Otherwise, expand one child and return it
        node.explore();
        if (!node.children().isEmpty()) {
            return node.children().iterator().next();
        }

        return node;
    }

    Node<TicTacToe> getBestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparingDouble(this::uct))
                .orElseThrow(() -> new IllegalStateException("No best child found"));
    }

    double uct(Node<TicTacToe> node) {
        if (node.playouts() == 0) {
            return Double.POSITIVE_INFINITY; // Ensure unvisited nodes are explored first
        }

        Node<TicTacToe> parent = node.getParent();
        double parentVisits = (parent != null) ? parent.playouts() : 1;

        // Exploitation term: win rate from this node's perspective
        double exploitation = node.wins() / node.playouts();

        // Exploration term: encourages visiting less-explored nodes
        double exploration = explorationParameter * Math.sqrt(Math.log(parentVisits) / node.playouts());

        return exploitation + exploration;
    }

    int simulate(Node<TicTacToe> node) {
        // Make a copy of the state to simulate on
        State<TicTacToe> state = node.state();

        // Continue until we reach a terminal state
        while (!state.isTerminal()) {
            List<Move<TicTacToe>> possibleMoves = new ArrayList<>(state.moves(state.player()));

            // Use strategic simulation instead of purely random
            Move<TicTacToe> selectedMove = findStrategicMove(state, possibleMoves);
            state = state.next(selectedMove);
        }

        // Return the winner (or -1 for a draw)
        return state.winner().orElse(-1);
    }

    Move<TicTacToe> findStrategicMove(State<TicTacToe> state, List<Move<TicTacToe>> moves) {
        int currentPlayer = state.player();
        int opponent = (currentPlayer == TicTacToe.X) ? TicTacToe.O : TicTacToe.X;

        // 1. Win immediately
        for (Move<TicTacToe> move : moves) {
            State<TicTacToe> nextState = state.next(move);
            if (nextState.isTerminal() && nextState.winner().orElse(-1) == currentPlayer)
                return move;
        }

// 2. Block opponent win
        for (Move<TicTacToe> move : moves) {
            State<TicTacToe> nextState = state.next(move);
            if (nextState.isTerminal()) continue; // no block needed

            for (Move<TicTacToe> opponentMove : nextState.moves(opponent)) {
                State<TicTacToe> opponentState = nextState.next(opponentMove);
                if (opponentState.isTerminal() && opponentState.winner().orElse(-1) == opponent)
                    return move;
            }
        }

        // 3. Prefer center
        for (Move<TicTacToe> move : moves) {
            int[] coords = ((TicTacToe.TicTacToeMove) move).move();
            if (coords[0] == 1 && coords[1] == 1) {
                return move;
            }
        }

        // 4. Prefer corners
        for (Move<TicTacToe> move : moves) {
            int[] coords = ((TicTacToe.TicTacToeMove) move).move();
            if ((coords[0] == 0 && coords[1] == 0) || // top-left
                    (coords[0] == 0 && coords[1] == 2) || // top-right
                    (coords[0] == 2 && coords[1] == 0) || // bottom-left
                    (coords[0] == 2 && coords[1] == 2)) { // bottom-right
                return move;
            }
        }

        // 5. Random move as fallback
        return moves.get(state.random().nextInt(moves.size()));
    }

    void backPropagate(Node<TicTacToe> node, int result) {
        // Traverse up the tree, updating statistics for each node
        while (node != null) {
            // Increment visit count
            node.setPlayouts(node.playouts() + 1);

            // Determine the perspective of the current node
            Node<TicTacToe> parent = node.getParent();
            int nodePerspectivePlayer;

            // If this is a root node
            if (parent == null) {
                // The perspective player is the one who just played
                nodePerspectivePlayer = (node.state().player() == TicTacToe.X) ? TicTacToe.O : TicTacToe.X;
            } else {
                // This node represents a move by the parent's player
                nodePerspectivePlayer = parent.state().player();
            }

            // Update wins based on the result
            double currentWins = node.wins();

            if (result == -1) {
                // Draw is worth 0.5 wins
                node.setWins(currentWins + 0.5);
            } else if (result == nodePerspectivePlayer) {
                // Win is worth 1.0
                node.setWins(currentWins + 1.0);
            }
            // Loss is worth 0.0 (no change needed)

            // Move up to parent
            node = node.getParent();
        }
    }

    public TicTacToeNode getBestMove() {
        // For the actual move selection, we prioritize the child with most visits
        // This is more robust than using UCT for the final decision
        return (TicTacToeNode) root.children().stream()
                .max(Comparator.comparing(Node::playouts))
                .orElseThrow(() -> new IllegalStateException("No children to choose from. Run MCTS first."));
    }
}