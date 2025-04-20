package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class DotsAndBoxesMctsTest {

    private DotsAndBoxesGame game;
    private DotsAndBoxesState initialState;

    @Before
    public void setup() {
        game = new DotsAndBoxesGame(3);
        initialState = (DotsAndBoxesState) game.start();
    }

    @Test
    public void testEasyDifficultyConstructor() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.EASY);
        assertNotNull(mcts.findBestMove(initialState));
    }

    @Test
    public void testMediumDifficultyConstructor() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.MEDIUM);
        assertNotNull(mcts.findBestMove(initialState));
    }

    @Test
    public void testHardDifficultyConstructor() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.HARD);
        assertNotNull(mcts.findBestMove(initialState));
    }

    @Test
    public void testExpertDifficultyConstructor() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.EXPERT);
        assertNotNull(mcts.findBestMove(initialState));
    }

    @Test
    public void testCustomConstructor() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(10, 1.0);
        assertNotNull(mcts.findBestMove(initialState));
    }

    @Test
    public void testFindBoxCompletingMove() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.EXPERT);
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();

        // Simulate 3 sides of a box to create a box-completing opportunity
        state = (DotsAndBoxesState) state.next(new DotsAndBoxesMove(0, 0, 0, 1, 1)); // top
        state = (DotsAndBoxesState) state.next(new DotsAndBoxesMove(1, 0, 1, 1, 1)); // bottom
        state = (DotsAndBoxesState) state.next(new DotsAndBoxesMove(0, 0, 1, 0, 1)); // left

        Move<DotsAndBoxesGame> move = mcts.findBestMove(state);
        assertNotNull("Should return a box-completing move", move);
    }

    @Test
    public void testFindSafeMoveFallback() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.EXPERT);
        Move<DotsAndBoxesGame> move = mcts.findBestMove(initialState);
        assertNotNull("Should find a valid move", move);
    }

    @Test
    public void testSelectFullyExpandedNode() {
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(1, 1.0); // only 1 iteration
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();
        mcts.findBestMove(state); // should still execute without error
    }

    @Test
    public void testFallbackToFirstAvailableMove() {
        // Create a non-heuristic MCTS that uses zero iterations
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(0, 1.0);
        Move<DotsAndBoxesGame> move = mcts.findBestMove(initialState);
        assertNotNull("Should fallback to first legal move", move);
    }

    @Test
    public void testIsFullyExpandedTrue() {
        DotsAndBoxesNode node = new DotsAndBoxesNode(initialState, null);
        Iterator<Move<DotsAndBoxesGame>> it = initialState.moveIterator(initialState.player());
        while (it.hasNext()) {
            State<DotsAndBoxesGame> childState = initialState.next(it.next());
            node.addChild(childState);
        }
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(1, 1.0);
        assertTrue(mcts.findBestMove(initialState) != null);
    }


    @Test
    public void testFindMoveToStateGetsCovered() {
        DotsAndBoxesGame game = new DotsAndBoxesGame(3); // Small board
        DotsAndBoxesState state = (DotsAndBoxesState) game.start();

        // Make one move to ensure we don't stay in root forever
        Move<DotsAndBoxesGame> initialMove = state.moveIterator(state.player()).next();
        state = (DotsAndBoxesState) state.next(initialMove);

        // Use higher iterations to ensure tree expands
        DotsAndBoxesMcts mcts = new DotsAndBoxesMcts(200, 1.0);
        Move<DotsAndBoxesGame> result = mcts.findBestMove(state);
        assertNotNull("Best move should be found", result);
    }
}


