
package com.phasmidsoftware.dsaipg.projects.mcts.dotsandboxes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

/**
 * GUI implementation for Dots and Boxes game (human vs AI)
 * Updated with difficulty level selection
 */
public class DotsAndBoxesGUI extends JFrame {
    private static final int CELL_SIZE = 60;
    private static final int DOT_SIZE = 10;
    private static final int LINE_THICKNESS = 4;
    private static final Color P1_COLOR = Color.BLUE;
    private static final Color P2_COLOR = Color.RED;
    private static final Color HIGHLIGHT_COLOR = Color.GREEN;

    private final DotsAndBoxesGame game;
    DotsAndBoxesState currentState;
    private DotsAndBoxesMcts ai;

    private final BoardPanel boardPanel;
    final JLabel statusLabel;
    private final JLabel scoreLabel;
    final JComboBox<DotsAndBoxesMcts.Difficulty> difficultySelector;

    private int selectedDotRow = -1;
    private int selectedDotCol = -1;

    /**
     * Create the GUI for Dots and Boxes
     */
    public DotsAndBoxesGUI(int boardSize) {
        // Create the game
        game = new DotsAndBoxesGame(boardSize);
        currentState = (DotsAndBoxesState) game.start();

        // Create the AI player with default Medium difficulty
        ai = new DotsAndBoxesMcts(DotsAndBoxesMcts.Difficulty.MEDIUM);

        // Setup the window
        setTitle("Dots and Boxes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the board panel
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(
                boardSize * CELL_SIZE + CELL_SIZE,
                boardSize * CELL_SIZE + CELL_SIZE));

        // Wrap boardPanel inside a centering panel
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        boardWrapper.add(boardPanel);

        // Create status label
        statusLabel = new JLabel("Your turn (Player 1)");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Create score label
        scoreLabel = new JLabel("Score - You: 0, AI: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Setup the layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(boardWrapper, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(statusLabel);
        infoPanel.add(scoreLabel);

        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        // Create the top control panel
        JPanel controlPanel = new JPanel();

        // Add restart button
        JButton restartButton = new JButton("New Game");
        restartButton.addActionListener(e -> resetGame());
        controlPanel.add(restartButton);

        // Add difficulty selector
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.add(new JLabel("AI Difficulty:"));
        difficultySelector = new JComboBox<>(DotsAndBoxesMcts.Difficulty.values());
        difficultySelector.setSelectedItem(DotsAndBoxesMcts.Difficulty.MEDIUM);
        difficultySelector.addActionListener(e -> {
            DotsAndBoxesMcts.Difficulty selectedDifficulty =
                    (DotsAndBoxesMcts.Difficulty) difficultySelector.getSelectedItem();
            ai = new DotsAndBoxesMcts(selectedDifficulty);
            statusLabel.setText("AI difficulty set to " + selectedDifficulty);
        });
        difficultyPanel.add(difficultySelector);
        controlPanel.add(difficultyPanel);

        contentPanel.add(controlPanel, BorderLayout.NORTH);

        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Reset the game to initial state
     */
    void resetGame() {
        currentState = (DotsAndBoxesState) game.start();
        selectedDotRow = -1;
        selectedDotCol = -1;
        statusLabel.setText("Your turn (Player 1)");
        updateScoreLabel();
        boardPanel.repaint();
    }

    /**
     * Update the score display
     */
    private void updateScoreLabel() {
        int[] scores = currentState.getScores();
        scoreLabel.setText("Score - You: " + scores[0] + ", AI: " + scores[1]);
    }

    /**
     * Make a move in the game
     */
    void makeMove(int row1, int col1, int row2, int col2) {
        // Create the move
        DotsAndBoxesMove move = new DotsAndBoxesMove(row1, col1, row2, col2, currentState.player());

        // Apply the move
        DotsAndBoxesState newState = (DotsAndBoxesState) currentState.next(move);

        // If the player completed a box, they get another turn
        boolean playerGetsTurn = currentState.player() == newState.player();

        // Update the current state
        currentState = newState;

        // Update status and score
        updateScoreLabel();

        if (currentState.isTerminal()) {
            announceWinner();
        } else {
            if (playerGetsTurn) {
                statusLabel.setText("You completed a box! Your turn again (Player 1)");
            } else {
                // AI's turn
                statusLabel.setText("AI is thinking...");

                // Use SwingWorker to run AI in background
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        // Slight delay so user can see what's happening
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        aiMove();
                        return null;
                    }
                };
                worker.execute();
            }
        }

        // Repaint the board
        boardPanel.repaint();
    }

    /**
     * Let the AI make a move
     */
    private void aiMove() {
        // If game is over, do nothing
        if (currentState.isTerminal()) {
            return;
        }

        // AI finds best move
        Move<DotsAndBoxesGame> bestMove = ai.findBestMove(currentState);
        DotsAndBoxesMove move = (DotsAndBoxesMove) bestMove;

        // Apply the move
        DotsAndBoxesState newState = (DotsAndBoxesState) currentState.next(move);

        // Check if AI gets another turn
        boolean aiGetsTurn = currentState.player() == newState.player();

        // Update current state
        currentState = newState;

        // Update status and score
        updateScoreLabel();

        SwingUtilities.invokeLater(() -> {
            if (currentState.isTerminal()) {
                announceWinner();
            } else {
                if (aiGetsTurn) {
                    statusLabel.setText("AI completed a box! AI's turn again");
                    // AI gets another turn
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            aiMove();
                            return null;
                        }
                    };
                    worker.execute();
                } else {
                    statusLabel.setText("Your turn (Player 1)");
                }
            }
            // Repaint the board
            boardPanel.repaint();
        });
    }

    /**
     * Announce the winner
     */
    void announceWinner() {
        int[] scores = currentState.getScores();
        Optional<Integer> winner = currentState.winner();

        if (winner.isPresent()) {
            if (winner.get() == 1) {
                statusLabel.setText("Game Over! You win! " + scores[0] + "-" + scores[1]);
            } else {
                statusLabel.setText("Game Over! AI wins! " + scores[1] + "-" + scores[0]);
            }
        } else {
            statusLabel.setText("Game Over! It's a tie! " + scores[0] + "-" + scores[1]);
        }
    }

    /**
     * Check if a move is valid
     */
    private boolean isValidMove(int row1, int col1, int row2, int col2) {
        // Only allow moves for player 1
        if (currentState.player() != 1) {
            return false;
        }

        // Check if dots are adjacent
        boolean isHorizontal = (row1 == row2 && Math.abs(col1 - col2) == 1);
        boolean isVertical = (col1 == col2 && Math.abs(row1 - row2) == 1);

        if (!isHorizontal && !isVertical) {
            return false;
        }

        // Check if line already exists
        boolean[][] horizontalLines = currentState.getHorizontalLines();
        boolean[][] verticalLines = currentState.getVerticalLines();

        if (isHorizontal) {
            int r = row1;
            int c = Math.min(col1, col2);
            return !horizontalLines[r][c];
        } else { // isVertical
            int r = Math.min(row1, row2);
            int c = col1;
            return !verticalLines[r][c];
        }
    }

    // Only for testing — can be marked with @VisibleForTesting if using Guava
    public int getSelectedDotRow() {
        return selectedDotRow;
    }

    public int getSelectedDotCol() {
        return selectedDotCol;
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }


    /**
     * Board panel for drawing the game
     */
    private class BoardPanel extends JPanel {

        public BoardPanel() {
            setBackground(Color.WHITE);

            // Add mouse listeners
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Only process clicks if it's player 1's turn
                    if (currentState.player() != 1 || currentState.isTerminal()) {
                        return;
                    }

                    // Calculate which dot was clicked
                    int size = game.getSize();
                    int margin = CELL_SIZE / 2;

                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            int dotX = margin + j * CELL_SIZE;
                            int dotY = margin + i * CELL_SIZE;

                            // Check if click is near a dot
                            if (Math.abs(e.getX() - dotX) < DOT_SIZE &&
                                    Math.abs(e.getY() - dotY) < DOT_SIZE) {

                                // First dot selected
                                if (selectedDotRow == -1) {
                                    selectedDotRow = i;
                                    selectedDotCol = j;
                                    repaint();
                                    return;
                                }

                                // Second dot selected
                                if (isValidMove(selectedDotRow, selectedDotCol, i, j)) {
                                    makeMove(selectedDotRow, selectedDotCol, i, j);
                                }

                                // Reset selection
                                selectedDotRow = -1;
                                selectedDotCol = -1;
                                repaint();
                                return;
                            }
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = game.getSize();
            int margin = CELL_SIZE / 2;

            // Get the game state
            boolean[][] horizontalLines = currentState.getHorizontalLines();
            boolean[][] verticalLines = currentState.getVerticalLines();
            int[][] boxes = currentState.getBoxes();

            for (int i = 0; i < size - 1; i++) {
                for (int j = 0; j < size - 1; j++) {
                    if (boxes[i][j] != 0) {
                        int x = margin + j * CELL_SIZE;
                        int y = margin + i * CELL_SIZE;

                        // Set color based on owner
                        if (boxes[i][j] == 1) {
                            g2d.setColor(new Color(P1_COLOR.getRed(), P1_COLOR.getGreen(), P1_COLOR.getBlue(), 100));
                        } else {
                            g2d.setColor(new Color(P2_COLOR.getRed(), P2_COLOR.getGreen(), P2_COLOR.getBlue(), 100));
                        }

                        // Fill the box
                        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                        // Draw the player number
                        g2d.setColor(boxes[i][j] == 1 ? P1_COLOR : P2_COLOR);
                        g2d.setFont(new Font("Arial", Font.BOLD, 20));
                        g2d.drawString(Integer.toString(boxes[i][j]),
                                x + CELL_SIZE/2 - 5,
                                y + CELL_SIZE/2 + 5);
                    }
                }
            }

            // Draw horizontal lines
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size - 1; j++) {
                    int x1 = margin + j * CELL_SIZE;
                    int y1 = margin + i * CELL_SIZE;
                    int x2 = margin + (j + 1) * CELL_SIZE;
                    int y2 = y1;

                    if (horizontalLines[i][j]) {
                        // Line already exists
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(LINE_THICKNESS));
                        g2d.drawLine(x1, y1, x2, y2);
                    } else {
                        // Potential line
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL, 0,
                                new float[]{5}, 0));
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            }

            // Draw vertical lines
            for (int i = 0; i < size - 1; i++) {
                for (int j = 0; j < size; j++) {
                    int x1 = margin + j * CELL_SIZE;
                    int y1 = margin + i * CELL_SIZE;
                    int x2 = x1;
                    int y2 = margin + (i + 1) * CELL_SIZE;

                    if (verticalLines[i][j]) {
                        // Line already exists
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(LINE_THICKNESS));
                        g2d.drawLine(x1, y1, x2, y2);
                    } else {
                        // Potential line
                        g2d.setColor(Color.LIGHT_GRAY);
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL, 0,
                                new float[]{5}, 0));
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            }

            // Draw dots
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int x = margin + j * CELL_SIZE;
                    int y = margin + i * CELL_SIZE;

                    // Check if this dot is selected
                    if (i == selectedDotRow && j == selectedDotCol) {
                        g2d.setColor(HIGHLIGHT_COLOR);
                        g2d.fillOval(x - DOT_SIZE, y - DOT_SIZE, DOT_SIZE * 2, DOT_SIZE * 2);
                    }

                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(x - DOT_SIZE/2, y - DOT_SIZE/2, DOT_SIZE, DOT_SIZE);
                }
            }
        }
    }

    /**
     * Main method to launch the game
     */
    public static void main(String[] args) {
        // Default board size
        int boardSize = 4;

        // Parse command line arguments if provided
        if (args.length > 0) {
            try {
                boardSize = Integer.parseInt(args[0]);
                // Ensure reasonable size
                boardSize = Math.max(3, Math.min(10, boardSize));
            } catch (NumberFormatException e) {
                System.err.println("Invalid board size, using default: " + boardSize);
            }
        }

        // Launch the GUI
        int finalBoardSize = boardSize;
        SwingUtilities.invokeLater(() -> {
            DotsAndBoxesGUI gui = new DotsAndBoxesGUI(finalBoardSize);
            gui.setVisible(true);
        });
    }
}
