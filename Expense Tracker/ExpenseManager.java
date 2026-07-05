import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Expense> expenses;
    private int nextId;
    private final String filePath;
    private Map<Integer, Double> monthlyBudgets;

    public ExpenseManager(String filePath) {
        this.filePath = filePath;
        this.expenses = new ArrayList<>();
        this.nextId = 1;
        this.monthlyBudgets = new HashMap<>();
        loadExpenses();
    }

    public int addExpense(String description, double amount, String category) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        Expense expense = new Expense(nextId++, description, amount, LocalDate.now(), category);
        expenses.add(expense);
        saveExpenses();
        checkBudget(expense);
        return expense.getId();
    }

    public void updateExpense(int id, String description, double amount, String category) {
        Expense expense = findById(id);
        if (expense == null) {
            throw new IllegalArgumentException("Expense with ID " + id + " not found.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (description != null)
            expense.setDescription(description);
        if (amount >= 0)
            expense.setAmount(amount);
        if (category != null)
            expense.setCategory(category);
        saveExpenses();
    }

    public void deleteExpense(int id) {
        Expense expense = findById(id);
        if (expense == null) {
            throw new IllegalArgumentException("Expense with ID " + id + " not found.");
        }
        expenses.remove(expense);
        saveExpenses();
    }

    public List<Expense> listExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> listExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> category.equalsIgnoreCase(e.getCategory()))
                .collect(Collectors.toList());
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getMonthlyTotal(int month) {
        return expenses.stream()
                .filter(e -> e.getDate().getMonthValue() == month && e.getDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getCategoryTotal(String category) {
        return expenses.stream()
                .filter(e -> category.equalsIgnoreCase(e.getCategory()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public void setBudget(int month, double budget) {
        monthlyBudgets.put(month, budget);
    }

    public Double getBudget(int month) {
        return monthlyBudgets.get(month);
    }

    public void exportToCsv(String exportPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(exportPath))) {
            writer.println("ID,Date,Description,Amount,Category");
            for (Expense e : expenses) {
                writer.printf("%d,%s,\"%s\",%.2f,%s%n",
                        e.getId(), e.getFormattedDate(), e.getDescription(),
                        e.getAmount(), e.getCategory() != null ? e.getCategory() : "");
            }
            System.out.println("Expenses exported to " + exportPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export expenses: " + e.getMessage());
        }
    }

    private Expense findById(int id) {
        return expenses.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    private void checkBudget(Expense expense) {
        int month = expense.getDate().getMonthValue();
        Double budget = monthlyBudgets.get(month);
        if (budget != null) {
            double monthlyTotal = getMonthlyTotal(month);
            if (monthlyTotal > budget) {
                System.out.printf("Warning: You have exceeded the budget for %s! (Total: $%.2f, Budget: $%.2f)%n",
                        Month.of(month), monthlyTotal, budget);
            } else if (monthlyTotal > budget * 0.9) {
                System.out.printf("Warning: You are approaching the budget for %s. (Total: $%.2f, Budget: $%.2f)%n",
                        Month.of(month), monthlyTotal, budget);
            }
        }
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("[");
            for (int i = 0; i < expenses.size(); i++) {
                writer.print(expenses.get(i).toJson());
                if (i < expenses.size() - 1)
                    writer.println(",");
            }
            writer.println("\n]");
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        File file = new File(filePath);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String json = content.toString().trim();
            if (json.isEmpty() || json.equals("[]"))
                return;

            json = json.substring(1, json.length() - 1).trim();
            if (json.isEmpty())
                return;

            String[] objects = json.split("\\}\\s*,\\s*\\{");
            for (String obj : objects) {
                obj = obj.replace("{", "").replace("}", "").trim();
                if (obj.isEmpty())
                    continue;

                int id = extractInt(obj, "\"id\"");
                String desc = extractString(obj, "\"description\"");
                double amount = extractDouble(obj, "\"amount\"");
                String dateStr = extractString(obj, "\"date\"");
                String category = extractOptionalString(obj, "\"category\"");

                LocalDate date = LocalDate.parse(dateStr);
                expenses.add(new Expense(id, desc, amount, date, category));
                if (id >= nextId)
                    nextId = id + 1;
            }
        } catch (Exception e) {
            System.err.println("Error loading expenses: " + e.getMessage());
        }
    }

    private String extractString(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1)
            return "";
        String after = json.substring(idx + key.length());
        int colonIdx = after.indexOf(':');
        if (colonIdx == -1)
            return "";
        after = after.substring(colonIdx + 1).trim();
        if (after.startsWith("\"")) {
            after = after.substring(1);
            int end = after.indexOf("\"");
            if (end == -1)
                return "";
            return after.substring(0, end);
        }
        int end = after.indexOf(',');
        if (end == -1)
            end = after.indexOf('}');
        if (end == -1)
            return after.trim();
        return after.substring(0, end).trim();
    }

    private String extractOptionalString(String json, String key) {
        String val = extractString(json, key);
        if (val.isEmpty() || val.equals("null"))
            return null;
        return val;
    }

    private int extractInt(String json, String key) {
        return Integer.parseInt(extractString(json, key));
    }

    private double extractDouble(String json, String key) {
        return Double.parseDouble(extractString(json, key));
    }
}
