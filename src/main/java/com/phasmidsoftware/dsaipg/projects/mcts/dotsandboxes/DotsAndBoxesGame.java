
package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

/**
 * Implementation of the Dots and Boxes game
 * Updated to support difficulty levels
 */
public class DotsAndBoxesGame implements Game<DotsAndBoxesGame> {
    private final int size;

    public DotsAndBoxesGame(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public State<DotsAndBoxesGame> start() {
        return new DotsAndBoxesState(this);
    }

    @Override
    public int opener() {
        return 1;
    }

    public static void main(String[] args) {
        int boardSize = 5; // Default board size

        //you can change the difficulty level to EASY/HARD/EXPERT
        DotsAndBoxesMcts.Difficulty difficulty = DotsAndBoxesMcts.Difficulty.MEDIUM;

        if (args.length > 0) {
            try {
                boardSize = Math.max(3, Math.min(10, Integer.parseInt(args[0])));
            } catch (NumberFormatException e) {
                System.err.println("Invalid board size, using default: " + boardSize);
            }
        }

        if (args.length > 1) {
            try {
                difficulty = DotsAndBoxesMcts.Difficulty.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid difficulty, using default: " + difficulty);
            }
        }

        DotsAndBoxesGame game = new DotsAndBoxesGame(boardSize);
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();

        System.out.println("Starting Dots and Boxes game:");
        System.out.println("Board size: " + boardSize + "x" + boardSize);
        System.out.println("AI Difficulty: " + difficulty);
        System.out.println();

        Simulator simulator = new Simulator(state, difficulty);
        simulator.run();
    }
}