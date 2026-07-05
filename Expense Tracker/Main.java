import java.util.List;

public class Main {
    private static final String DATA_FILE = "expenses.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        ExpenseManager manager = new ExpenseManager(DATA_FILE);
        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "add":
                    handleAdd(manager, args);
                    break;
                case "update":
                    handleUpdate(manager, args);
                    break;
                case "delete":
                    handleDelete(manager, args);
                    break;
                case "list":
                    handleList(manager, args);
                    break;
                case "summary":
                    handleSummary(manager, args);
                    break;
                case "budget":
                    handleBudget(manager, args);
                    break;
                case "export":
                    handleExport(manager, args);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    printUsage();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void handleAdd(ExpenseManager manager, String[] args) {
        String description = getArg(args, "--description");
        String amountStr = getArg(args, "--amount");
        String category = getArg(args, "--category");

        if (description == null || description.isEmpty()) {
            System.out.println("Error: --description is required.");
            return;
        }
        if (amountStr == null || amountStr.isEmpty()) {
            System.out.println("Error: --amount is required.");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int id = manager.addExpense(description, amount, category);
        System.out.println("Expense added successfully (ID: " + id + ")");
    }

    private static void handleUpdate(ExpenseManager manager, String[] args) {
        String idStr = getArg(args, "--id");
        if (idStr == null) {
            System.out.println("Error: --id is required.");
            return;
        }

        int id = Integer.parseInt(idStr);
        String description = getArg(args, "--description");
        String amountStr = getArg(args, "--amount");
        String category = getArg(args, "--category");

        double amount = amountStr != null ? Double.parseDouble(amountStr) : -1;
        manager.updateExpense(id, description, amount, category);
        System.out.println("Expense updated successfully (ID: " + id + ")");
    }

    private static void handleDelete(ExpenseManager manager, String[] args) {
        String idStr = getArg(args, "--id");
        if (idStr == null) {
            System.out.println("Error: --id is required.");
            return;
        }

        int id = Integer.parseInt(idStr);
        manager.deleteExpense(id);
        System.out.println("Expense deleted successfully (ID: " + id + ")");
    }

    private static void handleList(ExpenseManager manager, String[] args) {
        String category = getArg(args, "--category");
        List<Expense> expenses;

        if (category != null) {
            expenses = manager.listExpensesByCategory(category);
            System.out.println("Expenses in category '" + category + "':");
        } else {
            expenses = manager.listExpenses();
            System.out.println("All expenses:");
        }

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        System.out.printf("%-4s %-12s %-20s %-10s %s%n", "ID", "Date", "Description", "Amount", "Category");
        System.out.println("-".repeat(70));
        for (Expense e : expenses) {
            System.out.printf("%-4d %-12s %-20s $%-9.2f %s%n",
                    e.getId(), e.getFormattedDate(), e.getDescription(),
                    e.getAmount(), e.getCategory() != null ? e.getCategory() : "");
        }
    }

    private static void handleSummary(ExpenseManager manager, String[] args) {
        String monthStr = getArg(args, "--month");
        String category = getArg(args, "--category");

        if (category != null) {
            double total = manager.getCategoryTotal(category);
            System.out.printf("Total expenses for category '%s': $%.2f%n", category, total);
        } else if (monthStr != null) {
            int month = Integer.parseInt(monthStr);
            if (month < 1 || month > 12) {
                System.out.println("Error: Month must be between 1 and 12.");
                return;
            }
            double total = manager.getMonthlyTotal(month);
            java.time.Month monthName = java.time.Month.of(month);
            System.out.printf("Total expenses for %s: $%.2f%n", monthName, total);
        } else {
            double total = manager.getTotalExpenses();
            System.out.printf("Total expenses: $%.2f%n", total);
        }
    }

    private static void handleBudget(ExpenseManager manager, String[] args) {
        String monthStr = getArg(args, "--month");
        String amountStr = getArg(args, "--amount");

        if (monthStr == null || amountStr == null) {
            System.out.println("Error: --month and --amount are required.");
            return;
        }

        int month = Integer.parseInt(monthStr);
        double budget = Double.parseDouble(amountStr);

        if (month < 1 || month > 12) {
            System.out.println("Error: Month must be between 1 and 12.");
            return;
        }

        manager.setBudget(month, budget);
        java.time.Month monthName = java.time.Month.of(month);
        System.out.printf("Budget set for %s: $%.2f%n", monthName, budget);
    }

    private static void handleExport(ExpenseManager manager, String[] args) {
        String path = getArg(args, "--path");
        if (path == null) {
            path = "expenses_export.csv";
        }
        manager.exportToCsv(path);
    }

    private static String getArg(String[] args, String key) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(key)) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static void printUsage() {
        System.out.println("Usage: expense-tracker <command> [options]");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  add       --description <desc> --amount <amt> [--category <cat>]");
        System.out.println("  update    --id <id> [--description <desc>] [--amount <amt>] [--category <cat>]");
        System.out.println("  delete    --id <id>");
        System.out.println("  list      [--category <cat>]");
        System.out.println("  summary   [--month <1-12>] [--category <cat>]");
        System.out.println("  budget    --month <1-12> --amount <amt>");
        System.out.println("  export    [--path <file.csv>]");
    }
}
