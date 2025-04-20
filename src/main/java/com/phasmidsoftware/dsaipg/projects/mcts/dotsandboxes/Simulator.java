package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.Optional;

/**
 * Simulator for running Dots and Boxes games with a single MCTS AI instance
 */
public class Simulator {
    private DotsAndBoxesState currentState;
    private final DotsAndBoxesMcts mcts;
    private static final int MOVE_DELAY_MS = 500; // Delay between moves for visualization
    private int moveCount;

    /**
     * Create a simulator with the given starting state (Medium difficulty)
     */
    public Simulator(DotsAndBoxesState initialState) {
        this(initialState, DotsAndBoxesMcts.Difficulty.MEDIUM);
    }

    /**
     * Create a simulator with specified difficulty
     */
    public Simulator(DotsAndBoxesState initialState, DotsAndBoxesMcts.Difficulty difficulty) {
        this.currentState = initialState;
        this.mcts = new DotsAndBoxesMcts(difficulty);
        this.moveCount = 0;
    }

    /**
     * Run the simulation until the game is complete
     */
    public void run() {
        System.out.println("Starting Dots and Boxes game simulation");
        System.out.println("Board size: " + currentState.game().getSize() + "x" + currentState.game().getSize());
        //System.out.println("MCTS Difficulty: " + mcts.difficulty);
        System.out.println();

        moveCount = 1;

        // Continue until the game is over
        while (!currentState.isTerminal()) {
            // Display current board state
            System.out.println("Move #" + moveCount);
            displayBoard();

            // Get best move from MCTS
            Move<DotsAndBoxesGame> bestMove = mcts.findBestMove(currentState);

            if (bestMove == null) {
                System.out.println("No valid moves available - game ending");
                break;
            }

            System.out.println("Selected move: " + bestMove);

            // Apply the move
            currentState = (DotsAndBoxesState) currentState.next(bestMove);

            // Add a delay for visualization
            try {
                Thread.sleep(MOVE_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            incrementMoveCount();
        }

        // Show final state
        System.out.println("Final board state:");
        displayBoard();
        announceWinner();
    }

    /**
     * Display the current board state
     */
    protected void displayBoard() {
        int size = currentState.game().getSize();
        boolean[][] horizontalLines = currentState.getHorizontalLines();
        boolean[][] verticalLines = currentState.getVerticalLines();
        int[][] boxes = currentState.getBoxes();

        for (int i = 0; i < size; i++) {
            // Print dots and horizontal lines
            for (int j = 0; j < size; j++) {
                System.out.print("•"); // Dot

                // Print horizontal line if it exists
                if (j < size - 1) {
                    System.out.print(horizontalLines[i][j] ? "—" : " ");
                }
            }
            System.out.println();

            // Print vertical lines and box owners
            if (i < size - 1) {
                for (int j = 0; j < size; j++) {
                    // Print vertical line if it exists
                    System.out.print(verticalLines[i][j] ? "|" : " ");

                    // Print box owner if applicable
                    if (j < size - 1) {
                        if (boxes[i][j] == 1) {
                            System.out.print("1");
                        } else if (boxes[i][j] == 2) {
                            System.out.print("2");
                        } else {
                            System.out.print(" ");
                        }
                    }
                }
                System.out.println();
            }
        }

        // Print scores
        int[] scores = currentState.getScores();
        System.out.println("Score - Player 1: " + scores[0] + ", Player 2: " + scores[1]);
        System.out.println();
    }

    /**
     * Announce the winner at the end of the game
     */
    protected void announceWinner() {
        int[] scores = currentState.getScores();
        System.out.println("Game Over!");
        System.out.println("Final Score:");
        System.out.println("Player 1: " + scores[0]);
        System.out.println("Player 2: " + scores[1]);

        Optional<Integer> winner = currentState.winner();
        if (winner.isPresent()) {
            System.out.println("Player " + winner.get() + " wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }

    /**
     * Increment the move counter
     */
    protected void incrementMoveCount() {
        moveCount++;
    }

    /**
     * Get the current state
     */
    protected DotsAndBoxesState getCurrentState() {
        return currentState;
    }

    /**
     * Get the total number of moves played
     */
    public int getMoveCount() {
        return moveCount;
    }
}
