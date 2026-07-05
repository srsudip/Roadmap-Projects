# Number Guessing Game (Java)

A CLI-based number guessing game where you try to guess a randomly selected number between 1 and 100. Built as part of the **[roadmap.sh Number Guessing Game challenge](https://roadmap.sh/projects/number-guessing-game)**.

## 📝 Project Description
The computer picks a random number and you guess it. Choose your difficulty, get hints, and try to beat your high score!

**Challenge Link:** [https://roadmap.sh/projects/number-guessing-game](https://roadmap.sh/projects/number-guessing-game)

## ✨ Features
- **Three Difficulty Levels:** Easy (10 chances), Medium (5 chances), Hard (3 chances)
- **Hint System:** Type `hint` to get clues about odd/even and number range — no attempt cost
- **Multiple Rounds:** Play as many rounds as you like, quit anytime
- **High Scores:** Tracks fewest attempts per difficulty across rounds
- **Input Validation:** Handles non-numeric and out-of-range inputs gracefully
- **Quit Anytime:** Type `quit` to end the current round early

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 11** or higher

### Compilation
```bash
javac NumberGuessingGame.java Main.java
```

### Run the Game
```bash
java Main
```

## 🎮 How to Play

| Action | Input |
|:---|:---|
| **Select difficulty** | `1` (Easy), `2` (Medium), or `3` (Hard) |
| **Make a guess** | Any number between 1 and 100 |
| **Get a hint** | `hint` (free, no attempt used) |
| **Quit the round** | `quit` |
| **Play again** | `yes` or `no` after a round ends |

### Sample Game

```
========================================
   Welcome to the Number Guessing Game!
========================================

I'm thinking of a number between 1 and 100.
Try to guess it within the allowed number of attempts!

Please select the difficulty level:
1. Easy (10 chances)
2. Medium (5 chances)
3. Hard (3 chances)

Enter your choice: 2

Great! You have selected the Medium difficulty level.
You have 5 chances to guess the correct number.
Let's start the game!

Enter your guess (1-100), 'hint' for a clue, or 'quit': 50
Incorrect! The number is greater than 50. (4 chances remaining)

Enter your guess (1-100), 'hint' for a clue, or 'quit': hint
Hint: The number is odd.
Hint: The number is between 60 and 80.

Enter your guess (1-100), 'hint' for a clue, or 'quit': 70
Incorrect! The number is less than 70. (3 chances remaining)

Enter your guess (1-100), 'hint' for a clue, or 'quit': 63
Congratulations! You guessed the correct number in 3 attempts. (Time: 12 seconds)
```

## 🛠 Technical Implementation
- **Language:** Java (JDK 11+)
- **No External Libraries:** Pure Java with standard library only
- **Architecture:** Separated game logic (`NumberGuessingGame.java`) from CLI interaction (`Main.java`)

## 📂 Project Structure
```text
Number Guessing Game/
├── Main.java                # Entry point & CLI interaction
├── NumberGuessingGame.java  # Game logic, difficulty, hints, high scores
└── README.md                # This file
```
