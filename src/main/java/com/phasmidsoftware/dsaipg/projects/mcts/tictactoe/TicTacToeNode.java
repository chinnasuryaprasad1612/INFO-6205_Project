
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TicTacToeNode implements Node<TicTacToe> {

    private final State<TicTacToe> state;
    private final ArrayList<Node<TicTacToe>> children;
    private final Node<TicTacToe> parent;
    private double wins;
    private int playouts;
    private List<Move<TicTacToe>> unexploredMoves;

    /**
     * Constructor for creating a root node
     */
    public TicTacToeNode(State<TicTacToe> state) {
        this(state, null);
    }

    /**
     * Constructor for creating a child node with a reference to its parent
     */
    public TicTacToeNode(State<TicTacToe> state, Node<TicTacToe> parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.wins = 0;
        this.playouts = 0;

        // Initialize unexplored moves if the state is not terminal
        if (!state.isTerminal()) {
            this.unexploredMoves = new ArrayList<>(state.moves(state.player()));
        } else {
            this.unexploredMoves = new ArrayList<>();
        }
    }

    /**
     * @return true if this node is a leaf node (in which case no further exploration is possible).
     */
    @Override
    public boolean isLeaf() {
        return state.isTerminal() || !unexploredMoves.isEmpty();
    }

    /**
     * @return the State of the Game G that this Node represents.
     */
    @Override
    public State<TicTacToe> state() {
        return state;
    }

    /**
     * Explore this node by selecting and expanding an unexplored move
     */
    @Override
    public void explore() {
        if (!unexploredMoves.isEmpty()) {
            // Select a random unexplored move
            int index = state.random().nextInt(unexploredMoves.size());
            Move<TicTacToe> move = unexploredMoves.remove(index);

            // Create a new state by applying the move
            State<TicTacToe> childState = state.next(move);

            // Create a new child node
            TicTacToeNode childNode = new TicTacToeNode(childState, this);

            // Add the child node to children
            children.add(childNode);
        }
    }

    /**
     * @return the children of this Node.
     */
    @Override
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    /**
     * Method to determine if the player who plays to this node is the opening player.
     *
     * @return true if this node represents a move by the opening player.
     */
    public boolean white() {
        return state.player() == state.game().opener();
    }

    /**
     * @return the score for this Node (how many wins occurred in simulations from this position)
     */
    @Override
    public double wins() {
        return wins;
    }

    /**
     * @return the number of playouts evaluated through this node
     */
    @Override
    public int playouts() {
        return playouts;
    }

    /**
     * Update the win score for this node
     */
    @Override
    public void setWins(double wins) {
        this.wins = wins;
    }

    /**
     * Update the playout count for this node
     */
    @Override
    public void setPlayouts(int playouts) {
        this.playouts = playouts;
    }

    /**
     * @return the parent of this node
     */
    @Override
    public Node<TicTacToe> getParent() {
        return parent;
    }

    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<TicTacToe> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }
    public void addChild(State<TicTacToe> state) {
        children.add(new TicTacToeNode(state));
    }
}