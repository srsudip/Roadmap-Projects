import java.time.LocalDateTime;

public class Task {
    private int id;
    private String description;
    private TaskStatus status;
    private String createdAt; // Changed to String for easier JSON handling
    private String updatedAt; // Changed to String

    // Constructor for new tasks
    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.status = TaskStatus.todo;
        // Get current time and turn it into a readable String
        this.createdAt = LocalDateTime.now().toString();
        this.updatedAt = this.createdAt;
    }

    // Constructor for loading from JSON (Used by TaskManager)
    public Task(int id, String description, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.description = description;
        // Convert "in-progress" back to "in_progress" so Enum works
        this.status = TaskStatus.valueOf(status.replace("-", "_"));
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        // Format for the CLI output
        return String.format("[%d] %-20s | Status: %-12s | Created: %s",
                id, description, status.name().replace("_", "-"), createdAt);
    }

    // Getters so TaskManager can access private fields
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus s) {
        this.status = s;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
