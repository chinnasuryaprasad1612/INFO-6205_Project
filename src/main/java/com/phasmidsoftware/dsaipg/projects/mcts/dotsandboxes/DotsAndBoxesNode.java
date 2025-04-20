package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Node implementation for Dots and Boxes MCTS
 */
public class DotsAndBoxesNode implements Node<DotsAndBoxesGame> {
    private final State<DotsAndBoxesGame> state;
    private final Node<DotsAndBoxesGame> parent;
    private final Collection<Node<DotsAndBoxesGame>> children;
    private double wins;
    private int playouts;

    /**
     * Create a new node
     * @param state the game state at this node
     * @param parent the parent node (null for root)
     */
    public DotsAndBoxesNode(State<DotsAndBoxesGame> state, Node<DotsAndBoxesGame> parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.wins = 0;
        this.playouts = 0;

        // For terminal nodes, evaluate immediately
        if (state.isTerminal()) {
            this.playouts = 1;
            Optional<Integer> winner = state.winner();

            if (winner.isPresent()) {
                // Score from perspective of player 1 (considered "white")
                this.wins = winner.get() == 1 ? 2 : 0;
            } else {
                // Draw is worth 1 point
                this.wins = 1;
            }
        }
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<DotsAndBoxesGame> state() {
        return state;
    }

    @Override
    public boolean white() {
        // Player 1 is considered "white" (first player)
        return state.player() == 1;
    }

    @Override
    public Collection<Node<DotsAndBoxesGame>> children() {
        return children;
    }

    @Override
    public void backPropagate() {
        if (children.isEmpty()) return;

        double totalWins = 0;
        int totalPlayouts = 0;

        for (Node<DotsAndBoxesGame> child : children) {
            // Score from parent's perspective is opposite of child's perspective
            totalWins += 2 - child.wins();
            totalPlayouts += child.playouts();
        }

        this.wins = totalWins;
        this.playouts = totalPlayouts;
    }

    @Override
    public void addChild(State<DotsAndBoxesGame> state) {
        Node<DotsAndBoxesGame> child = new DotsAndBoxesNode(state, this);
        children.add(child);
    }

    @Override
    public double wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    @Override
    public void setPlayouts(int playouts) {
        this.playouts = playouts;
    }

    @Override
    public Node<DotsAndBoxesGame> getParent() {
        return parent;
    }

    @Override
    public void setWins(double wins) {
        this.wins = wins;
    }

    /**
     * Select the best child using UCT formula
     * @param explorationParameter the constant that controls exploration vs exploitation
     * @return the best child according to UCT
     */
    public Node<DotsAndBoxesGame> selectBestChild(double explorationParameter) {
        if (children.isEmpty()) {
            return null;
        }

        Node<DotsAndBoxesGame> bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Node<DotsAndBoxesGame> child : children) {
            // Skip nodes with no playouts
            if (child.playouts() == 0) continue;

            // Calculate UCT value: exploitation + exploration
            double exploitation = child.wins() / child.playouts();
            double exploration = explorationParameter * Math.sqrt(Math.log(this.playouts) / child.playouts());
            double uctValue = exploitation + exploration;

            if (uctValue > bestValue) {
                bestValue = uctValue;
                bestChild = child;
            }
        }

        return bestChild;
    }
}