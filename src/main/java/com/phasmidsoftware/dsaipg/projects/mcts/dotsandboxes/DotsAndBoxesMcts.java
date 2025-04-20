package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.Iterator;

/**
 * Simplified MCTS for Dots and Boxes (no time limit, single instance use)
 */
public class DotsAndBoxesMcts {
    public enum Difficulty {
        EASY(100, 1.0, false),
        MEDIUM(1000, Math.sqrt(2), true),
        HARD(5000, Math.sqrt(2), true),
        EXPERT(15000, Math.sqrt(2), true);

        final int iterations;
        final double explorationParam;
        final boolean useHeuristics;

        Difficulty(int iterations, double explorationParam, boolean useHeuristics) {
            this.iterations = iterations;
            this.explorationParam = explorationParam;
            this.useHeuristics = useHeuristics;
        }
    }

    private final int iterations;
    private final double explorationParam;
    private final boolean useHeuristics;
    final Difficulty difficulty;

    public DotsAndBoxesMcts(Difficulty difficulty) {
        this.iterations = difficulty.iterations;
        this.explorationParam = difficulty.explorationParam;
        this.useHeuristics = difficulty.useHeuristics;
        this.difficulty = difficulty;
    }
    public DotsAndBoxesMcts(int iterations, double explorationParam) {
        this.iterations = iterations;
        this.explorationParam = explorationParam;
        this.useHeuristics = true;
        this.difficulty = null;
    }
    public Move<DotsAndBoxesGame> findBestMove(State<DotsAndBoxesGame> state) {
        if (useHeuristics) {
            Move<DotsAndBoxesGame> move = findBoxCompletingMove(state);
            if (move != null) return move;
            if (difficulty == Difficulty.HARD || difficulty == Difficulty.EXPERT) {
                Move<DotsAndBoxesGame> safeMove = findSafeMove(state);
                if (safeMove != null) return safeMove;
            }
        }

        Node<DotsAndBoxesGame> rootNode = new DotsAndBoxesNode(state, null);
        for (int i = 0; i < iterations; i++) {
            Node<DotsAndBoxesGame> selectedNode = select(rootNode);
        }

        Node<DotsAndBoxesGame> bestChild = findBestChild(rootNode);
        if (bestChild != null) {
            return findMoveToState(state, bestChild.state());
        }

        Iterator<Move<DotsAndBoxesGame>> moves = state.moveIterator(state.player());
        return moves.hasNext() ? moves.next() : null;
    }

    private Move<DotsAndBoxesGame> findBoxCompletingMove(State<DotsAndBoxesGame> state) {
        DotsAndBoxesState db = (DotsAndBoxesState) state;
        int size = db.game().getSize();
        boolean[][] h = db.getHorizontalLines();
        boolean[][] v = db.getVerticalLines();
        int player = db.player();

        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                int count = 0;
                if (h[i][j]) count++;
                if (h[i + 1][j]) count++;
                if (v[i][j]) count++;
                if (v[i][j + 1]) count++;
                if (count == 3) {
                    if (!h[i][j]) return new DotsAndBoxesMove(i, j, i, j + 1, player);
                    if (!h[i + 1][j]) return new DotsAndBoxesMove(i + 1, j, i + 1, j + 1, player);
                    if (!v[i][j]) return new DotsAndBoxesMove(i, j, i + 1, j, player);
                    if (!v[i][j + 1]) return new DotsAndBoxesMove(i, j + 1, i + 1, j + 1, player);
                }
            }
        }
        return null;
    }

    private Move<DotsAndBoxesGame> findSafeMove(State<DotsAndBoxesGame> state) {
        for (Iterator<Move<DotsAndBoxesGame>> it = state.moveIterator(state.player()); it.hasNext(); ) {
            Move<DotsAndBoxesGame> move = it.next();
            if (findBoxCompletingMove(state.next(move)) == null) return move;
        }
        return null;
    }

    private Node<DotsAndBoxesGame> select(Node<DotsAndBoxesGame> node) {
        if (node.isLeaf()) {
            node.explore();
            return node;
        }
        if (!isFullyExpanded(node)) {
            node.explore();
            return node;
        }
        DotsAndBoxesNode dbNode = (DotsAndBoxesNode) node;
        Node<DotsAndBoxesGame> bestChild = dbNode.selectBestChild(explorationParam);
        return bestChild == null ? node : select(bestChild);
    }

    private boolean isFullyExpanded(Node<DotsAndBoxesGame> node) {
        int count = 0;
        for (Iterator<Move<DotsAndBoxesGame>> it = node.state().moveIterator(node.state().player()); it.hasNext(); ) {
            it.next();
            count++;
        }
        return node.children().size() >= count;
    }

    private Node<DotsAndBoxesGame> findBestChild(Node<DotsAndBoxesGame> node) {
        Node<DotsAndBoxesGame> best = null;
        double bestRate = Double.NEGATIVE_INFINITY;
        for (Node<DotsAndBoxesGame> child : node.children()) {
            if (child.playouts() == 0) continue;
            double rate = child.wins() / child.playouts();
            if (rate > bestRate) {
                best = child;
                bestRate = rate;
            }
        }
        return best;
    }

    private Move<DotsAndBoxesGame> findMoveToState(State<DotsAndBoxesGame> from, State<DotsAndBoxesGame> to) {
        for (Iterator<Move<DotsAndBoxesGame>> it = from.moveIterator(from.player()); it.hasNext(); ) {
            Move<DotsAndBoxesGame> move = it.next();
            if (from.next(move).equals(to)) return move;
        }
        return null;
    }
}


