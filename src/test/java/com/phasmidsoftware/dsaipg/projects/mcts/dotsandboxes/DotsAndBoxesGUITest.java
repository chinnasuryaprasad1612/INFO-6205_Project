package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class DotsAndBoxesGUITest {

    private DotsAndBoxesGUI gui;

    @Before
    public void setUp() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> gui = new DotsAndBoxesGUI(4));
    }

    @After
    public void tearDown() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            gui.setVisible(false);
            gui.dispose();
        });
    }

    @Test
    public void testGUIInitialization() {
        assertNotNull(gui);
        assertTrue(gui.isDisplayable());
        assertEquals("Dots and Boxes", gui.getTitle());
    }

    @Test
    public void testDifficultySelectionUpdatesAI() {
        SwingUtilities.invokeLater(() -> {
            JComboBox combo = getDifficultySelector(gui);
            assertNotNull(combo);
            combo.setSelectedItem(DotsAndBoxesMcts.Difficulty.EXPERT);
            assertEquals(DotsAndBoxesMcts.Difficulty.EXPERT, combo.getSelectedItem());
        });
    }

    private JComboBox getDifficultySelector(DotsAndBoxesGUI gui) {
        for (Component c : gui.getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JPanel) {
                        for (Component nested : ((JPanel) inner).getComponents()) {
                            if (nested instanceof JComboBox) {
                                return (JComboBox) nested;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    @Test
    public void testBoardPanelRendering() {
        Component[] components = gui.getContentPane().getComponents();
        boolean found = false;
        for (Component c : components) {
            if (c instanceof JPanel) {
                for (Component inner : ((JPanel) c).getComponents()) {
                    if (inner instanceof JPanel) {
                        found = true;
                        break;
                    }
                }
            }
        }
        assertTrue(found);
    }

    @Test
    public void testMainMethodLaunch() {
        SwingUtilities.invokeLater(() -> DotsAndBoxesGUI.main(new String[]{"4"}));

    }



    @Test
    public void testIsValidMoveHorizontalAndVertical() {
        assertTrue(guiIsValidMove(gui, 0, 0, 0, 1)); // horizontal
        assertTrue(guiIsValidMove(gui, 0, 0, 1, 0)); // vertical
        assertFalse(guiIsValidMove(gui, 0, 0, 1, 1)); // diagonal - invalid
        assertFalse(guiIsValidMove(gui, 0, 0, 0, 0)); // same point - invalid
    }


    @Test
    public void testAnnounceWinnerUpdatesStatus() {
        // fill all boxes and assign to player 1
        int size = gui.currentState.game().getSize();
        for (int i = 0; i < size - 1; i++)
            for (int j = 0; j < size - 1; j++)
                gui.currentState.getBoxes()[i][j] = 1;

        gui.announceWinner();
        assertTrue(gui.statusLabel.getText().contains("win") || gui.statusLabel.getText().contains("tie"));
    }

    // Utility to access private method isValidMove via reflection
    private boolean guiIsValidMove(DotsAndBoxesGUI gui, int r1, int c1, int r2, int c2) {
        try {
            java.lang.reflect.Method m = DotsAndBoxesGUI.class.getDeclaredMethod("isValidMove", int.class, int.class, int.class, int.class);
            m.setAccessible(true);
            return (boolean) m.invoke(gui, r1, c1, r2, c2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testResetGameFunctionality() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            gui.makeMove(0, 0, 0, 1); // make a move
            gui.resetGame();
            assertNotNull(gui); // GUI is still initialized
            assertEquals("Your turn (Player 1)", gui.statusLabel.getText()); // Status label is reset
        });
    }
    @Test
    public void testDifficultyChangeUpdatesAI() throws Exception {
        JComboBox comboBox = gui.difficultySelector;
        comboBox.setSelectedItem(DotsAndBoxesMcts.Difficulty.EXPERT);

        Field aiField = DotsAndBoxesGUI.class.getDeclaredField("ai");
        aiField.setAccessible(true);
        DotsAndBoxesMcts aiInstance = (DotsAndBoxesMcts) aiField.get(gui);

        assertEquals(DotsAndBoxesMcts.Difficulty.EXPERT, aiInstance.difficulty);
    }

    @Test
    public void testFirstDotSelection() {
        DotsAndBoxesGUI gui = new DotsAndBoxesGUI(4);
        gui.setVisible(false); // skip rendering

        JPanel boardPanel = gui.getBoardPanel(); // no reflection
        int margin = 60 / 2;
        int x = margin + 0 * 60;
        int y = margin + 0 * 60;

        MouseEvent click = new MouseEvent(boardPanel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false);
        boardPanel.dispatchEvent(click);

        assertEquals(0, gui.getSelectedDotRow());
        assertEquals(0, gui.getSelectedDotCol());
    }
    @Test
    public void testTwoDotClicksTriggersMoveAndResetsSelection() {
        DotsAndBoxesGUI gui = new DotsAndBoxesGUI(4);
        gui.setVisible(false); // Skip window rendering

        JPanel board = gui.getBoardPanel();
        int margin = 60 / 2;

        // First click at (0,0)
        int x1 = margin + 0 * 60;
        int y1 = margin + 0 * 60;
        MouseEvent firstClick = new MouseEvent(board, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x1, y1, 1, false);
        board.dispatchEvent(firstClick);

        // Second click at (0,1) — adjacent horizontal dot
        int x2 = margin + 1 * 60;
        int y2 = margin + 0 * 60;
        MouseEvent secondClick = new MouseEvent(board, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x2, y2, 1, false);
        board.dispatchEvent(secondClick);

        // Now verify the selection was reset
        assertEquals(-1, getIntField(gui, "selectedDotRow"));
        assertEquals(-1, getIntField(gui, "selectedDotCol"));
    }
    private int getIntField(Object obj, String name) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void testSecondDotClickTriggersValidMoveAndResetsSelection() {
        DotsAndBoxesGUI gui = new DotsAndBoxesGUI(4); // 4x4 board
        gui.setVisible(false); // prevent showing UI

        JPanel board = gui.getBoardPanel(); // <- Add public getter in GUI
        int margin = 60 / 2;

        // First click at dot (0,0)
        int x1 = margin + 0 * 60;
        int y1 = margin + 0 * 60;
        MouseEvent click1 = new MouseEvent(board, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x1, y1, 1, false);
        board.dispatchEvent(click1);

        // Second click at adjacent dot (0,1)
        int x2 = margin + 1 * 60;
        int y2 = margin + 0 * 60;
        MouseEvent click2 = new MouseEvent(board, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x2, y2, 1, false);
        board.dispatchEvent(click2);

        // After second click, selection should be reset
        assertEquals(-1, gui.getSelectedDotRow()); // <- Add public getter in GUI
        assertEquals(-1, gui.getSelectedDotCol());
    }

}

