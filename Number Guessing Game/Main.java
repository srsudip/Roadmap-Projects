import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final NumberGuessingGame game = new NumberGuessingGame();

    public static void main(String[] args) {
        printWelcome();

        boolean playAgain = true;
        while (playAgain) {
            playRound();
            playAgain = askPlayAgain();
        }

        printGoodbye();
        scanner.close();
    }

    private static void printWelcome() {
        System.out.println("========================================");
        System.out.println("   Welcome to the Number Guessing Game!");
        System.out.println("========================================");
        System.out.println();
        System.out.println("I'm thinking of a number between 1 and 100.");
        System.out.println("Try to guess it within the allowed number of attempts!");
        System.out.println();
        System.out.println("Rules:");
        System.out.println("  - You select a difficulty level which determines your chances.");
        System.out.println("  - After each guess, you'll be told if the number is higher or lower.");
        System.out.println("  - Type 'hint' instead of a number to get a clue (costs no attempt).");
        System.out.println("  - Type 'quit' to exit the game at any time.");
        System.out.println();
    }

    private static void playRound() {
        NumberGuessingGame.Difficulty difficulty = selectDifficulty();

        game.startNewRound(difficulty);

        System.out.println();
        System.out.printf("Great! You have selected the %s difficulty level.%n", difficulty.getDisplayName());
        System.out.printf("You have %d chances to guess the correct number.%n", difficulty.getMaxChances());
        System.out.println("Let's start the game!");
        System.out.println();

        while (!game.isGameOver()) {
            System.out.printf("Enter your guess (1-100), 'hint' for a clue, or 'quit': ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                System.out.println("You quit the round. The number was " + game.getTargetNumber() + ".");
                return;
            }

            if (input.equalsIgnoreCase("hint")) {
                System.out.println(game.getHint());
                System.out.println(game.getRangeHint());
                System.out.println();
                continue;
            }

            int guess;
            try {
                guess = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 100.");
                continue;
            }

            if (guess < 1 || guess > 100) {
                System.out.println("Please enter a number between 1 and 100.");
                continue;
            }

            NumberGuessingGame.GuessResult result = game.makeGuess(guess);
            System.out.println(result.getMessage());
            System.out.println();

            if (result.isCorrect()) {
                printHighScores();
            }
        }
    }

    private static NumberGuessingGame.Difficulty selectDifficulty() {
        System.out.println("Please select the difficulty level:");
        System.out.println("1. Easy (10 chances)");
        System.out.println("2. Medium (5 chances)");
        System.out.println("3. Hard (3 chances)");
        System.out.println();

        while (true) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 3) {
                    return NumberGuessingGame.Difficulty.fromChoice(choice);
                }
            } catch (NumberFormatException e) {
                // fall through
            }

            System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }
    }

    private static boolean askPlayAgain() {
        System.out.println();
        System.out.print("Would you like to play again? (yes/no): ");

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes") || input.equals("y")) {
                System.out.println();
                return true;
            }
            if (input.equals("no") || input.equals("n")) {
                return false;
            }
            System.out.print("Please enter 'yes' or 'no': ");
        }
    }

    private static void printHighScores() {
        System.out.println("--- High Scores ---");
        for (NumberGuessingGame.Difficulty d : NumberGuessingGame.Difficulty.values()) {
            int score = game.getHighScore(d);
            if (score < Integer.MAX_VALUE) {
                System.out.printf("  %s: %d attempt%s%n", d.getDisplayName(), score, score == 1 ? "" : "s");
            } else {
                System.out.printf("  %s: No score yet%n", d.getDisplayName());
            }
        }
        System.out.println("-------------------");
    }

    private static void printGoodbye() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("   Thanks for playing!");
        System.out.println("========================================");
        System.out.println();
        printHighScores();
        System.out.println();
        System.out.println("See you next time!");
    }
}
