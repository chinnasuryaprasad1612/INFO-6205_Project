package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void testMoveThrowsOnOccupied() {
        Position pos = Position.parsePosition("X . .\n. O .\n. . X", 1);
        assertThrows(RuntimeException.class, () -> pos.move(1, 0, 0));
    }

    @Test
    void testMoveThrowsOnFullBoard() {
        Position pos = Position.parsePosition("X X 0\nX O 0\nX X 0", 1);
        assertThrows(RuntimeException.class, () -> pos.move(0, 0, 1));
    }

    @Test
    void testValidMoveUpdatesBoard() {
        Position pos = Position.parsePosition("X . .\n. O .\n. . X", 1);
        Position moved = pos.move(0, 0, 1);
        assertEquals(Position.parsePosition("X O .\n. O .\n. . X", 0), moved);
    }

    @Test
    void testMovesListSizeAndOrder() {
        Position pos = Position.parsePosition("X . .\n. O .\n. . X", 1);
        List<int[]> moves = pos.moves(0);
        assertEquals(6, moves.size());
        assertArrayEquals(new int[]{0, 1}, moves.get(0));
    }

    @Test
    void testReflectHorizontally() {
        Position pos = Position.parsePosition("X O .\n. X .\n. . O", 1);
        Position reflected = pos.reflect(0);
        assertEquals(Position.parsePosition(". . O\n. X .\nX O .", 1), reflected);
    }

    @Test
    void testReflectVertically() {
        Position pos = Position.parsePosition("X O .\n. X .\n. . O", 1);
        Position reflected = pos.reflect(1);
        assertEquals(Position.parsePosition(". O X\n. X .\nO . .", 1), reflected);
    }


    @Test
    void testWinnerDetectionRow() {
        Position pos = Position.parsePosition("X X X\n. O .\n. . O", 1);
        assertEquals(Optional.of(1), pos.winner());
    }

    @Test
    void testWinnerDetectionColumn() {
        Position pos = Position.parsePosition("X . .\nX O .\nX . O", 1);
        assertEquals(Optional.of(1), pos.winner());
    }

    @Test
    void testWinnerDetectionDiagonal() {
        Position pos = Position.parsePosition("O . X\n. X .\nX . O", 1);
        assertEquals(Optional.of(1), pos.winner());
    }

    @Test
    void testWinnerEmpty() {
        Position pos = Position.parsePosition("X . .\n. O .\n. . X", 1);
        assertTrue(pos.winner().isEmpty());
    }

    @Test
    void testThreeInARow() {
        Position pos = Position.parsePosition("X X X\nO O .\n. . .", 1);
        assertTrue(pos.threeInARow());
    }

    @Test
    void testProjectRowAndCol() {
        Position pos = Position.parsePosition("X . 0\nX O .\nX . 0", 1);
        assertArrayEquals(new int[]{1, -1, 0}, pos.projectRow(0));
        assertArrayEquals(new int[]{1, 1, 1}, pos.projectCol(0));
    }

    @Test
    void testProjectDiagonals() {
        Position pos = Position.parsePosition("X . 0\nX O .\nX . 0", 1);
        assertArrayEquals(new int[]{1, 0, 0}, pos.projectDiag(true));
        assertArrayEquals(new int[]{1, 0, 0}, pos.projectDiag(false));
    }

    @Test
    void testParseCell() {
        assertEquals(0, Position.parseCell("0"));
        assertEquals(0, Position.parseCell("o"));
        assertEquals(1, Position.parseCell("1"));
        assertEquals(1, Position.parseCell("X"));
        assertEquals(-1, Position.parseCell("."));
    }

    @Test
    void testFullBoard() {
        assertTrue(Position.parsePosition("X O X\nO X O\nX X O", 1).full());
        assertFalse(Position.parsePosition("X . .\n. O .\n. . X", 1).full());
    }

    @Test
    void testRenderToString() {
        String grid = "X . .\n. O .\n. . X";
        Position pos = Position.parsePosition(grid, 1);
        assertEquals(grid, pos.render());
        assertEquals("1,-1,-1\n-1,0,-1\n-1,-1,1", pos.toString());
    }
}
