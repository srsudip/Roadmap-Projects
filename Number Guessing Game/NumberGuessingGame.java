import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NumberGuessingGame {

    public enum Difficulty {
        EASY(10, "Easy"),
        MEDIUM(5, "Medium"),
        HARD(3, "Hard");

        private final int maxChances;
        private final String displayName;

        Difficulty(int maxChances, String displayName) {
            this.maxChances = maxChances;
            this.displayName = displayName;
        }

        public int getMaxChances() {
            return maxChances;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Difficulty fromChoice(int choice) {
            switch (choice) {
                case 1: return EASY;
                case 2: return MEDIUM;
                case 3: return HARD;
                default: return MEDIUM;
            }
        }
    }

    private final Random random;
    private final Map<Difficulty, Integer> highScores;
    private int targetNumber;
    private Difficulty currentDifficulty;
    private int attempts;
    private long startTime;
    private boolean gameOver;

    public NumberGuessingGame() {
        this.random = new Random();
        this.highScores = new HashMap<>();
        this.highScores.put(Difficulty.EASY, Integer.MAX_VALUE);
        this.highScores.put(Difficulty.MEDIUM, Integer.MAX_VALUE);
        this.highScores.put(Difficulty.HARD, Integer.MAX_VALUE);
    }

    public void startNewRound(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.targetNumber = random.nextInt(100) + 1;
        this.attempts = 0;
        this.startTime = System.currentTimeMillis();
        this.gameOver = false;
    }

    public GuessResult makeGuess(int guess) {
        if (gameOver) {
            return new GuessResult(false, 0, "Game is already over.");
        }

        attempts++;

        if (guess == targetNumber) {
            gameOver = true;
            long elapsed = getElapsedTime();
            updateHighScore();
            return new GuessResult(true, attempts,
                    String.format("Congratulations! You guessed the correct number in %d attempt%s. (Time: %d seconds)",
                            attempts, attempts == 1 ? "" : "s", elapsed / 1000));
        }

        if (attempts >= currentDifficulty.getMaxChances()) {
            gameOver = true;
            return new GuessResult(false, attempts,
                    String.format("Game Over! You've used all %d chances. The number was %d.", attempts, targetNumber));
        }

        if (guess < targetNumber) {
            return new GuessResult(false, attempts,
                    String.format("Incorrect! The number is greater than %d. (%d chance%s remaining)",
                            guess, currentDifficulty.getMaxChances() - attempts,
                            currentDifficulty.getMaxChances() - attempts == 1 ? "" : "s"));
        } else {
            return new GuessResult(false, attempts,
                    String.format("Incorrect! The number is less than %d. (%d chance%s remaining)",
                            guess, currentDifficulty.getMaxChances() - attempts,
                            currentDifficulty.getMaxChances() - attempts == 1 ? "" : "s"));
        }
    }

    public String getHint() {
        if (gameOver) {
            return "Game is already over.";
        }

        int range = currentDifficulty == Difficulty.EASY ? 20 : currentDifficulty == Difficulty.MEDIUM ? 10 : 5;

        if (targetNumber % 2 == 0) {
            return "Hint: The number is even.";
        } else {
            return "Hint: The number is odd.";
        }
    }

    public String getRangeHint() {
        if (gameOver) {
            return "Game is already over.";
        }

        int lower = Math.max(1, targetNumber - 10);
        int upper = Math.min(100, targetNumber + 10);
        return String.format("Hint: The number is between %d and %d.", lower, upper);
    }

    private long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    private void updateHighScore() {
        if (attempts < highScores.get(currentDifficulty)) {
            highScores.put(currentDifficulty, attempts);
        }
    }

    public int getHighScore(Difficulty difficulty) {
        return highScores.get(difficulty);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public int getAttempts() {
        return attempts;
    }

    public long getElapsedTimeMs() {
        return getElapsedTime();
    }

    public static class GuessResult {
        private final boolean correct;
        private final int attempts;
        private final String message;

        public GuessResult(boolean correct, int attempts, String message) {
            this.correct = correct;
            this.attempts = attempts;
            this.message = message;
        }

        public boolean isCorrect() {
            return correct;
        }

        public int getAttempts() {
            return attempts;
        }

        public String getMessage() {
            return message;
        }
    }
}
