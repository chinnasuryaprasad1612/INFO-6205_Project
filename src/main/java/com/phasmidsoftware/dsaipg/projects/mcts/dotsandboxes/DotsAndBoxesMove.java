package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

/**
 * Implementation of a move in the Dots and Boxes game
 */
public class DotsAndBoxesMove implements Move<DotsAndBoxesGame> {
    // Store coordinates and line information
    private final int row1, col1, row2, col2;
    private final int playerNumber;

    /**
     * Constructor for a move in Dots and Boxes
     *
     * @param row1 row coordinate of first dot
     * @param col1 column coordinate of first dot
     * @param row2 row coordinate of second dot
     * @param col2 column coordinate of second dot
     * @param playerNumber the player making this move
     */
    public DotsAndBoxesMove(int row1, int col1, int row2, int col2, int playerNumber) {
        this.row1 = row1;
        this.col1 = col1;
        this.row2 = row2;
        this.col2 = col2;
        this.playerNumber = playerNumber;
    }

    /**
     * Required by Move interface to identify the player
     */
    @Override
    public int player() {
        return playerNumber;
    }

    /**
     * Helper method to check if this is a horizontal move
     * @return true if horizontal, false if vertical
     */
    public boolean isHorizontal() {
        return row1 == row2;
    }

    // Getters for accessing the move coordinates
    public int getRow1() { return row1; }
    public int getCol1() { return col1; }
    public int getRow2() { return row2; }
    public int getCol2() { return col2; }

    @Override
    public String toString() {
        return String.format("Move by Player %d: (%d,%d) to (%d,%d)",
                playerNumber, row1, col1, row2, col2);
    }
}