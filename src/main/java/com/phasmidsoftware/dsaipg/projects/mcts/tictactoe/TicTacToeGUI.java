package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class TicTacToeGUI extends JFrame {
    private final JButton[][] buttons = new JButton[3][3];
    private final JPanel gamePanel = new JPanel();
    private final JLabel statusLabel = new JLabel("Player X's turn", JLabel.CENTER);
    private final JButton resetButton = new JButton("New Game");
    private JComboBox<String> difficultyComboBox;

    private TicTacToe game;
    private TicTacToeNode root;
    private int currentPlayer;
    private boolean gameOver = false;
    private int difficulty = 1000; // Default difficulty (iterations for MCTS)

    public TicTacToeGUI() {
        setTitle("Tic Tac Toe with MCTS AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        // Create difficulty selector
        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel difficultyLabel = new JLabel("AI Difficulty: ");
        String[] difficultyLevels = {"Easy", "Medium", "Hard", "Expert"};
        difficultyComboBox = new JComboBox<>(difficultyLevels);
        difficultyComboBox.setSelectedIndex(1); // Default to Medium
        difficultyComboBox.addActionListener(e -> updateDifficulty());

        controlPanel.add(difficultyLabel);
        controlPanel.add(difficultyComboBox);
        controlPanel.add(resetButton);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Set up game panel
        gamePanel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 40));
                buttons[i][j].setFocusPainted(false);
                final int row = i;
                final int col = j;
                buttons[i][j].addActionListener(e -> handlePlayerMove(row, col));
                gamePanel.add(buttons[i][j]);
            }
        }
        add(gamePanel, BorderLayout.CENTER);

        resetButton.addActionListener(e -> resetGame());

        initializeGame();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateDifficulty() {
        int index = difficultyComboBox.getSelectedIndex();
        switch (index) {
            case 0: // Easy
                difficulty = 100;
                break;
            case 1: // Medium
                difficulty = 1000;
                break;
            case 2: // Hard
                difficulty = 5000;
                break;
            case 3: // Expert
                difficulty = 15000;
                break;
        }
        statusLabel.setText("Difficulty set to " + difficultyComboBox.getSelectedItem());
    }

    private void initializeGame() {
        game = new TicTacToe();
        currentPlayer = TicTacToe.X; // Player is X (human)
        root = new TicTacToeNode(game.new TicTacToeState());
        updateButtons();
        gameOver = false;
        statusLabel.setText("Your turn (X)");
    }

    void resetGame() {
        initializeGame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(null);
            }
        }
    }

    void handlePlayerMove(int row, int col) {
        if (gameOver || buttons[row][col].getText().length() > 0) {
            return; // Ignore if game is over or cell is occupied
        }

        // Player's move (X)
        makeMove(row, col, TicTacToe.X);

        if (!gameOver) {
            // AI's turn
            SwingUtilities.invokeLater(this::makeAIMove);
        }
    }

    private void makeMove(int row, int col, int player) {
        // Update game state
        TicTacToe.TicTacToeMove move = new TicTacToe.TicTacToeMove(player, row, col);
        root = new TicTacToeNode(root.state().next(move));

        updateButtons();
        checkGameStatus();
    }

    private void makeAIMove() {
        statusLabel.setText("AI is thinking...");

        // Show "thinking" animation
        new Thread(() -> {
            try {
                // Run MCTS to find the best move
                MCTS mcts = new MCTS(root);
                mcts.run(difficulty);
                root = mcts.getBestMove();

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    updateButtons();
                    checkGameStatus();
                    if (!gameOver) {
                        statusLabel.setText("Your turn (X)");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        statusLabel.setText("Error in AI move: " + e.getMessage()));
            }
        }).start();
    }

    void checkGameStatus() {
        Optional<Integer> winner = root.state().winner();
        if (root.state().isTerminal()) {
            gameOver = true;
            if (winner.isPresent()) {
                int winnerPlayer = winner.get();
                String winnerStr = (winnerPlayer == TicTacToe.X) ? "X (You)" : "O (AI)";
                statusLabel.setText("Game over! Winner: " + winnerStr);
                highlightWinningCells();
            } else {
                statusLabel.setText("Game over! It's a draw.");
            }
        }
    }

    void highlightWinningCells() {
        Position position = ((TicTacToe.TicTacToeState)root.state()).position();
        int[][] board = getBoard();

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != -1 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                buttons[i][0].setBackground(Color.GREEN);
                buttons[i][1].setBackground(Color.GREEN);
                buttons[i][2].setBackground(Color.GREEN);
                return;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != -1 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                buttons[0][j].setBackground(Color.GREEN);
                buttons[1][j].setBackground(Color.GREEN);
                buttons[2][j].setBackground(Color.GREEN);
                return;
            }
        }

        // Check diagonals
        if (board[0][0] != -1 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            buttons[0][0].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][2].setBackground(Color.GREEN);
            return;
        }

        if (board[0][2] != -1 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            buttons[0][2].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][0].setBackground(Color.GREEN);
        }
    }

    private int[][] getBoard() {
        int[][] board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String text = buttons[i][j].getText();
                if (text.equals("X")) {
                    board[i][j] = TicTacToe.X;
                } else if (text.equals("O")) {
                    board[i][j] = TicTacToe.O;
                } else {
                    board[i][j] = -1; // Empty
                }
            }
        }
        return board;
    }

    private void updateButtons() {
        // This converts the current game state to button text
        String boardStr = root.state().toString();

        // Parse the board representation
        String[] lines = boardStr.split("\\n");

        // The board string is in the format "TicTacToe{\n-1,-1,-1\n-1,-1,-1\n-1,-1,-1\n}"
        // We need to extract just the board part
        for (int i = 1; i < 4; i++) {  // Lines 1, 2, 3 contain the board
            if (i-1 < 3) {  // Safety check
                String[] cells = lines[i].split(",");
                for (int j = 0; j < cells.length; j++) {
                    String cellValue = cells[j].trim();
                    if (cellValue.equals("1")) {
                        buttons[i-1][j].setText("X");
                    } else if (cellValue.equals("0")) {
                        buttons[i-1][j].setText("O");
                    } else {
                        buttons[i-1][j].setText("");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}