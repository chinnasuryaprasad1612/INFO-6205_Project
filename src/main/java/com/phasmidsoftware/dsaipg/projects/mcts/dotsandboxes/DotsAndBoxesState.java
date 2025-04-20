package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

/**
 * Represents a state of the Dots and Boxes game
 */
public class DotsAndBoxesState implements State<DotsAndBoxesGame> {
    // The game this state belongs to
    private final DotsAndBoxesGame game;

    // Board state
    private final boolean[][] horizontalLines;
    private final boolean[][] verticalLines;
    private final int[][] boxes;
    private final int[] scores;

    // Current player
    private final int currentPlayer;

    // Random source for move generation
    private final Random random;

    /**
     * Constructor for initial game state
     */
    public DotsAndBoxesState(DotsAndBoxesGame game) {
        this.game = game;
        int size = game.getSize();

        // Initialize empty board
        this.horizontalLines = new boolean[size][size - 1];
        this.verticalLines = new boolean[size - 1][size];
        this.boxes = new int[size - 1][size - 1];
        this.scores = new int[] {0, 0};

        // First player starts
        this.currentPlayer = game.opener();

        // Initialize random source
        this.random = new Random();
    }
    public DotsAndBoxesState(DotsAndBoxesGame game, int startingPlayer) {
        this.game = game;
        int size = game.getSize();

        this.horizontalLines = new boolean[size][size - 1];
        this.verticalLines = new boolean[size - 1][size];
        this.boxes = new int[size - 1][size - 1];
        this.scores = new int[] {0, 0};

        // Use passed-in player
        this.currentPlayer = startingPlayer;

        this.random = new Random();
    }

    /**
     * Constructor for a state after a move
     */
    private DotsAndBoxesState(DotsAndBoxesGame game, boolean[][] horizontalLines, boolean[][] verticalLines,
                              int[][] boxes, int[] scores, int currentPlayer, Random random) {
        this.game = game;
        this.horizontalLines = horizontalLines;
        this.verticalLines = verticalLines;
        this.boxes = boxes;
        this.scores = scores;
        this.currentPlayer = currentPlayer;
        this.random = random;
    }

    @Override
    public DotsAndBoxesGame game() {
        return game;
    }

    @Override
    public boolean isTerminal() {
        // Game is over when all boxes are claimed
        int size = game.getSize();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (boxes[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int player() {
        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {
        if (!isTerminal()) {
            return Optional.empty();
        }

        if (scores[0] > scores[1]) {
            return Optional.of(1); // Player 1 wins
        } else if (scores[1] > scores[0]) {
            return Optional.of(2); // Player 2 wins
        } else {
            return Optional.empty(); // Draw
        }
    }

    @Override
    public Random random() {
        return random;
    }

    @Override
    public Collection<Move<DotsAndBoxesGame>> moves(int player) {
        List<Move<DotsAndBoxesGame>> validMoves = new ArrayList<>();
        int size = game.getSize();

        // Add horizontal lines that are not yet placed
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (!horizontalLines[i][j]) {
                    validMoves.add(new DotsAndBoxesMove(i, j, i, j + 1, player));
                }
            }
        }

        // Add vertical lines that are not yet placed
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size; j++) {
                if (!verticalLines[i][j]) {
                    validMoves.add(new DotsAndBoxesMove(i, j, i + 1, j, player));
                }
            }
        }

        return validMoves;
    }

    @Override
    public State<DotsAndBoxesGame> next(Move<DotsAndBoxesGame> move) {
        DotsAndBoxesMove dbMove = (DotsAndBoxesMove) move;
        int size = game.getSize();

        // Create deep copies of the current board state
        boolean[][] newHorizontalLines = copyArray(horizontalLines);
        boolean[][] newVerticalLines = copyArray(verticalLines);
        int[][] newBoxes = copyArray(boxes);
        int[] newScores = Arrays.copyOf(scores, scores.length);

        // Apply the move
        boolean completedBox = placeLine(dbMove, newHorizontalLines, newVerticalLines, newBoxes, newScores);

        // Determine next player - if a box was completed, same player goes again
        int nextPlayer = completedBox ? currentPlayer : (currentPlayer == 1 ? 2 : 1);

        // Create and return new state
        return new DotsAndBoxesState(game, newHorizontalLines, newVerticalLines, newBoxes, newScores, nextPlayer, random);
    }

    /**
     * Copy a 2D boolean array
     */
    private boolean[][] copyArray(boolean[][] array) {
        boolean[][] copy = new boolean[array.length][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return copy;
    }

    /**
     * Copy a 2D int array
     */
    private int[][] copyArray(int[][] array) {
        int[][] copy = new int[array.length][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return copy;
    }

    /**
     * Place a line on the board and check if it completes any boxes
     * @return true if at least one box was completed
     */
    private boolean placeLine(DotsAndBoxesMove move, boolean[][] horizontalLines, boolean[][] verticalLines,
                              int[][] boxes, int[] scores) {
        boolean completedBox = false;
        int size = game.getSize();

        if (move.isHorizontal()) {
            // Place horizontal line
            int r = move.getRow1();
            int c = Math.min(move.getCol1(), move.getCol2());
            horizontalLines[r][c] = true;

            // Check if this completes any boxes
            if (r > 0 && checkBox(r - 1, c, horizontalLines, verticalLines, boxes, move.player())) {
                scores[move.player() - 1]++;
                completedBox = true;
            }

            if (r < size - 1 && checkBox(r, c, horizontalLines, verticalLines, boxes, move.player())) {
                scores[move.player() - 1]++;
                completedBox = true;
            }
        } else {
            // Place vertical line
            int r = Math.min(move.getRow1(), move.getRow2());
            int c = move.getCol1();
            verticalLines[r][c] = true;

            // Check if this completes any boxes
            if (c > 0 && checkBox(r, c - 1, horizontalLines, verticalLines, boxes, move.player())) {
                scores[move.player() - 1]++;
                completedBox = true;
            }

            if (c < size - 1 && checkBox(r, c, horizontalLines, verticalLines, boxes, move.player())) {
                scores[move.player() - 1]++;
                completedBox = true;
            }
        }

        return completedBox;
    }

    /**
     * Check if a box is completed and claim it if it is
     * @return true if the box was completed
     */
    private boolean checkBox(int row, int col, boolean[][] horizontalLines, boolean[][] verticalLines,
                             int[][] boxes, int player) {
        // A box is completed if all four sides have lines
        if (horizontalLines[row][col] &&               // Top
                horizontalLines[row + 1][col] &&           // Bottom
                verticalLines[row][col] &&                 // Left
                verticalLines[row][col + 1]) {             // Right

            // Only claim if not already claimed
            if (boxes[row][col] == 0) {
                boxes[row][col] = player;
                return true;
            }
        }
        return false;
    }

    /**
     * Get the horizontal lines
     */
    public boolean[][] getHorizontalLines() {
        return horizontalLines;
    }

    /**
     * Get the vertical lines
     */
    public boolean[][] getVerticalLines() {
        return verticalLines;
    }

    /**
     * Get the boxes
     */
    public int[][] getBoxes() {
        return boxes;
    }

    /**
     * Get the scores
     */
    public int[] getScores() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DotsAndBoxesState that)) return false;

        // Compare all elements of the board state
        return Arrays.deepEquals(horizontalLines, that.horizontalLines) &&
                Arrays.deepEquals(verticalLines, that.verticalLines) &&
                Arrays.deepEquals(boxes, that.boxes) &&
                Arrays.equals(scores, that.scores) &&
                currentPlayer == that.currentPlayer;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(game, currentPlayer);
        result = 31 * result + Arrays.deepHashCode(horizontalLines);
        result = 31 * result + Arrays.deepHashCode(verticalLines);
        result = 31 * result + Arrays.deepHashCode(boxes);
        result = 31 * result + Arrays.hashCode(scores);
        return result;
    }
}