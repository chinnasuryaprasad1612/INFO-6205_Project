package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TicTacToeGUITest {

    private TicTacToeGUI gui;

    @BeforeEach
    public void setup() throws Exception {
        SwingUtilities.invokeAndWait(() -> gui = new TicTacToeGUI());
    }

    @Test
    public void testConstructor() {
        assertNotNull(gui);
    }

    @Test
    public void testUpdateDifficulty() throws Exception {
        JComboBox<?> box = getField("difficultyComboBox");
        box.setSelectedIndex(3); // Expert
        assertEquals(15000, getIntField("difficulty"));
    }

    @Test
    public void testResetButton() throws Exception {
        JButton resetButton = getField("resetButton");
        resetButton.doClick();
        assertFalse(getBooleanField("gameOver"));
    }

    @Test
    public void testHandlePlayerMoveAndMakeMove() throws Exception {
        JButton[][] buttons = getField("buttons");
        buttons[0][0].doClick(); // simulate player click
        String text = buttons[0][0].getText();
        assertTrue(text.equals("X") || text.equals("O"));
    }


    @Test
    public void testHighlightWinningCells_RowWin() throws Exception {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState winState = game.new TicTacToeState(
                Position.parsePosition("X X X\n. . .\n. . .", TicTacToe.O));
        setField("root", new TicTacToeNode(winState));

        JButton[][] buttons = getField("buttons");
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText(winState.position().render().split("\n")[i].split(" ")[j]);

        invokePrivate("highlightWinningCells");

        Color expected = Color.GREEN;
        assertEquals(expected, buttons[0][0].getBackground());
        assertEquals(expected, buttons[0][1].getBackground());
        assertEquals(expected, buttons[0][2].getBackground());
    }

    @Test
    public void testMakeAIMoveDoesNotCrash() throws Exception {
        invokePrivate("makeAIMove");
        assertNotNull(getField("root"));
    }

    // Utility: Access private fields
    @SuppressWarnings("unchecked")
    private <T> T getField(String name) throws Exception {
        Field field = TicTacToeGUI.class.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(gui);
    }

    private boolean getBooleanField(String name) throws Exception {
        Field field = TicTacToeGUI.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getBoolean(gui);
    }

    private int getIntField(String name) throws Exception {
        Field field = TicTacToeGUI.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(gui);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = TicTacToeGUI.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(gui, value);
    }

    // Utility: Call private method
    private void invokePrivate(String methodName) throws Exception {
        var method = TicTacToeGUI.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(gui);
    }

    @Test
    public void testHighlightWinningCells_ColumnWin() throws Exception {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState winState = game.new TicTacToeState(
                Position.parsePosition("X . .\nX . .\nX . .", TicTacToe.O));
        setField("root", new TicTacToeNode(winState));

        JButton[][] buttons = getField("buttons");
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText(winState.position().render().split("\n")[i].split(" ")[j]);

        invokePrivate("highlightWinningCells");

        Color expected = Color.GREEN;
        assertEquals(expected, buttons[0][0].getBackground());
        assertEquals(expected, buttons[1][0].getBackground());
        assertEquals(expected, buttons[2][0].getBackground());
    }
    @Test
    public void testHighlightWinningCells_DiagonalWin() throws Exception {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState winState = game.new TicTacToeState(
                Position.parsePosition("X . .\n. X .\n. . X", TicTacToe.O));
        setField("root", new TicTacToeNode(winState));

        JButton[][] buttons = getField("buttons");
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText(winState.position().render().split("\n")[i].split(" ")[j]);

        invokePrivate("highlightWinningCells");

        Color expected = Color.GREEN;
        assertEquals(expected, buttons[0][0].getBackground());
        assertEquals(expected, buttons[1][1].getBackground());
        assertEquals(expected, buttons[2][2].getBackground());
    }
    @Test
    public void testHighlightWinningCells_AntiDiagonalWin() throws Exception {
        TicTacToe game = new TicTacToe();
        TicTacToe.TicTacToeState winState = game.new TicTacToeState(
                Position.parsePosition(". . X\n. X .\nX . .", TicTacToe.O));
        setField("root", new TicTacToeNode(winState));

        JButton[][] buttons = getField("buttons");
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText(winState.position().render().split("\n")[i].split(" ")[j]);

        invokePrivate("highlightWinningCells");

        Color expected = Color.GREEN;
        assertEquals(expected, buttons[0][2].getBackground());
        assertEquals(expected, buttons[1][1].getBackground());
        assertEquals(expected, buttons[2][0].getBackground());
    }
    @Test
    public void testSetDifficultyToEasy() throws Exception {
        JComboBox<?> box = getField("difficultyComboBox");
        box.setSelectedIndex(0); // Easy
        invokePrivate("updateDifficulty");
        assertEquals(100, getIntField("difficulty"));
        JLabel statusLabel = getField("statusLabel");
        assertTrue(statusLabel.getText().contains("Easy"));
    }

    @Test
    public void testSetDifficultyToMedium() throws Exception {
        JComboBox<?> box = getField("difficultyComboBox");
        box.setSelectedIndex(1); // Medium
        invokePrivate("updateDifficulty");
        assertEquals(1000, getIntField("difficulty"));
        JLabel statusLabel = getField("statusLabel");
        assertTrue(statusLabel.getText().contains("Medium"));
    }

    @Test
    public void testSetDifficultyToHard() throws Exception {
        JComboBox<?> box = getField("difficultyComboBox");
        box.setSelectedIndex(2); // Hard
        invokePrivate("updateDifficulty");
        assertEquals(5000, getIntField("difficulty"));
        JLabel statusLabel = getField("statusLabel");
        assertTrue(statusLabel.getText().contains("Hard"));
    }
}