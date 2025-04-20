# Monte Carlo Tree Search for Tic-Tac-Toe

This project implements a Monte Carlo Tree Search (MCTS) based AI to play the classic game of **Tic-Tac-Toe** and of **Dots and Boxes**.It includes a full simulation environment, a customizable number of iterations, and performance tracking through metrics.

---

## 🎯 Objective

To simulate intelligent decision-making using MCTS in the game of Tic-Tac-Toe and Dots and Boxes and evaluate its performance against itself and random strategies under varying simulation counts.

---
## 🧾 Game Rules

### Tic-Tac-Toe
- The game is played on a **3×3** board.
- There are **two players**, traditionally `X` and `O`.
- **X always goes first.**
- Players alternate turns, placing their mark (`X` or `O`) in an empty cell.
- The goal is to place **three of their marks in a horizontal, vertical, or diagonal row**.
- If all cells are filled and no player has achieved three in a row, the game ends in a **draw**.

### Dots and Boxes
- Played on an **NxN** grid of dots (typically 5×5 or 7×7).
- Players take turns drawing a **horizontal or vertical line** between two adjacent dots.
- When a player completes the **fourth side of a box**, they claim it and score **1 point**.
- A player who completes a box gets an **extra turn**.
- The game ends when **all possible lines are drawn**.
- The player who **owns the most boxes** at the end wins.
- If both players own the **same number of boxes**, the game ends in a **draw**.

📌 *Note:* In our implementation, Player 1 always starts and the score updates dynamically after each move.

[View the complete project documentation here](https://docs.google.com/document/d/1t6qhPidmGvUK8FZV1OEQQHFpwPfSJLltatpFFLa279A/edit?usp=sharing)


---

## ⚙️ Heuristics for tic-tac-toe game

Heuristic-based move selection is used in simulations to improve performance:
- Choose a winning move if available.
- Block opponent's winning move.
- Prefer the center cell `(1,1)` if open.
- Otherwise, select a random move.

---

## ⚙️ Heuristics for dots and boxes game
- Complete a box: If a move allows the player to complete a 4-sided box, it is selected immediately to score a point and earn another turn.
- Avoid giving away boxes: In higher difficulty levels (HARD, EXPERT), moves that would allow the opponent to complete a box in the next turn are avoided.

## 📊 Metrics Tracked

- `Iterations`: Simulations per move.
- `TotalGames`: Total number of matches played.
- `XWins` / `OWins`: Wins by each player.
- `Draws`: Draw outcomes.
- `WinRateX` / `WinRateO`: Win percentage.
- `AvgGameLength`: Average number of moves per game.
- `TimeMillis`: Time taken (ms) to complete all simulations.

CSV output is generated to evaluate MCTS performance over different iteration counts.

## 🚀 How to Run the Project

### ▶ Tic-Tac-Toe

- **Benchmarking**
  - Run the benchmarking experiments by executing:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/Benchmarks.java
    ```

- **Simulate a Single Game**
  - To run a single game simulation in the console, execute:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/TicTacToe.java
    ```

- **Play via GUI**
  - Launch the interactive GUI and play against the MCTS AI:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/TicTacToeGUI.java
    ```
  - Choose your **difficulty level** to adjust AI intelligence (EASY, MEDIUM, HARD, EXPERT).

---
### How to run

### ▶ Dots and Boxes
=======
- Navigate to TicTacToeStats.java and run that file to get benchmark results


- **Benchmarking**
  - Run the benchmarking experiments by executing:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/dotsandboxes/Benchmarks.java
    ```

- **Simulate a Single Game**
  - To run a single game simulation in the console, execute:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/dotsandboxes/DotsAndBoxesGame.java
    ```

- **Play via GUI**
  - Launch the interactive GUI and play against the MCTS AI:
    ```
    src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/dotsandboxes/DotsAndBoxesGUI.java
    ```
  - Choose your **difficulty level** to adjust AI intelligence (EASY, MEDIUM, HARD, EXPERT).







## 🙌 Acknowledgments

Project developed for **INFO 6205 PSA** at Northeastern University, inspired by algorithms from Prof. Robin Hillyard's PSA course.
