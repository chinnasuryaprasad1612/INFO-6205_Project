# Monte Carlo Tree Search for Tic-Tac-Toe

This project implements a Monte Carlo Tree Search (MCTS) based AI to play the classic game of **Tic-Tac-Toe**. It includes a full simulation environment, a customizable number of iterations, and performance tracking through metrics.

---

## 🎯 Objective

To simulate intelligent decision-making using MCTS in the game of Tic-Tac-Toe and evaluate its performance against itself and random strategies under varying simulation counts.

---

## 🧾 Game Rules

- The game is played on a **3×3** board.
- There are **two players**, traditionally `X` and `O`.
- **X always goes first.**
- Players alternate turns, placing their mark (`X` or `O`) in an empty cell.
- The goal is to place **three of their marks in a horizontal, vertical, or diagonal row**.
- If all cells are filled and no player has achieved three in a row, the game ends in a **draw**.

---

## 🧠 Implementation: Monte Carlo Tree Search (MCTS)

The MCTS algorithm is implemented with the following phases:

### 1. **Selection**
- Traverse the tree from the root node using the **UCT (Upper Confidence Bound applied to Trees)** formula to select the best child until a leaf node is reached.

### 2. **Expansion**
- If the selected node is not terminal and not fully expanded, generate all legal moves as child nodes.

### 3. **Simulation**
- From the expanded node, simulate a complete game using random or heuristic-based decisions until a terminal state is reached.

### 4. **Backpropagation**
- The result of the simulation is propagated back through the visited nodes, updating their statistics:
    - Win = +2
    - Draw = +1
    - Loss = +0

---

## ⚙️ Heuristics

Heuristic-based move selection is used in simulations to improve performance:
- Choose a winning move if available.
- Block opponent's winning move.
- Prefer the center cell `(1,1)` if open.
- Otherwise, select a random move.

---

## 📊 Metrics Tracked

- `Iterations`: Simulations per move.
- `TotalGames`: Total number of matches played.
- `XWins` / `OWins`: Wins by each player.
- `Draws`: Draw outcomes.
- `WinRateX` / `WinRateO`: Win percentage.
- `AvgGameLength`: Average number of moves per game.
- `TotalSimulations`: Simulations across all games.
- `AvgSimPerGame`: Avg. simulations per game.
- `TimeMillis`: Time taken (ms) to complete all simulations.

CSV output is generated to evaluate MCTS performance over different iteration counts.

---
### How to run

- Navigate to TicTacToeStats.java and run that file to get benchmark results




## 🧪 Test Cases

JUnit test suite covers:

- Valid MCTS simulations
- Correct playout outcomes
- Proper node selection and expansion
- MCTS vs Random and MCTS vs MCTS performance evaluation




## 🙌 Acknowledgments

Project developed for **INFO 6205 PSA** at Northeastern University, inspired by algorithms from Prof. Robin Hillyard's PSA course.
