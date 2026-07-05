import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense {
    private int id;
    private String description;
    private double amount;
    private LocalDate date;
    private String category;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Expense(int id, String description, double amount, LocalDate date, String category) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFormattedDate() {
        return date.format(DATE_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("%-4d %-12s %-20s $%-10.2f %s",
                id, getFormattedDate(), description, amount,
                category != null ? "[" + category + "]" : "");
    }

    public String toJson() {
        String cat = category != null ? "\"" + escapeJson(category) + "\"" : "null";
        return String.format("{\"id\":%d,\"description\":\"%s\",\"amount\":%.2f,\"date\":\"%s\",\"category\":%s}",
                id, escapeJson(description), amount, getFormattedDate(), cat);
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
