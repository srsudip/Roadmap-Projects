
// TaskManager.java 
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.time.LocalDateTime;

public class TaskManager {
    private final String fileName = "tasks.json";

    public TaskManager() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (!new File(fileName).exists())
                Files.writeString(Paths.get(fileName), "[]");
        } catch (IOException e) {
        }
    }

    public List<Task> loadAll() {
        List<Task> tasks = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(fileName)).trim();
            if (content.equals("[]") || content.isEmpty())
                return tasks;
            Pattern pattern = Pattern.compile(
                    "\\{\"id\":\\s*(\\d+),\\s*\"description\":\\s*\"([^\"]*)\",\\s*\"status\":\\s*\"([^\"]*)\",\\s*\"createdAt\":\\s*\"([^\"]*)\",\\s*\"updatedAt\":\\s*\"([^\"]*)\"\\}");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                tasks.add(new Task(Integer.parseInt(matcher.group(1)), matcher.group(2),
                        matcher.group(3).replace("-", "_"), matcher.group(4), matcher.group(5)));
            }
        } catch (Exception e) {
        }
        return tasks;
    }

    public void saveAll(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            sb.append(String.format(
                    "  {\"id\": %d, \"description\": \"%s\", \"status\": \"%s\", \"createdAt\": \"%s\", \"updatedAt\": \"%s\"}%s\n",
                    t.getId(), t.getDescription(), t.getStatus().name().replace("_", "-"),
                    t.getCreatedAt(), t.getUpdatedAt(), (i < tasks.size() - 1 ? "," : "")));
        }
        sb.append("]");
        try {
            Files.writeString(Paths.get(fileName), sb.toString());
        } catch (IOException e) {
        }
    }

    public void addTask(String desc) {
        List<Task> tasks = loadAll();
        int nextId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
        tasks.add(new Task(nextId, desc));
        saveAll(tasks);
        System.out.println("Task added successfully (ID: " + nextId + ")");
    }

    public void listTasks(String filter) {
        List<Task> tasks = loadAll();
        if (filter == null || filter.isEmpty()) {
            if (tasks.isEmpty())
                System.out.println("No tasks found.");
            else
                tasks.forEach(System.out::println);
        } else {
            String f = filter.replace("-", "_");
            boolean found = false;
            for (Task t : tasks) {
                if (t.getStatus().name().equals(f)) {
                    System.out.println(t);
                    found = true;
                }
            }
            if (!found)
                System.out.println("No tasks found.");
        }
    }

    public void updateTask(int id, String desc) {
        List<Task> tasks = loadAll();
        for (Task t : tasks) {
            if (t.getId() == id) {
                t.setDescription(desc);
                // Since we use Strings, we just manually refresh the timestamp string
                String now = LocalDateTime.now().toString();
                try {
                    java.lang.reflect.Field fUpdate = t.getClass().getDeclaredField("updatedAt");
                    fUpdate.setAccessible(true);
                    fUpdate.set(t, now);
                } catch (Exception e) {
                }
                saveAll(tasks);
                System.out.println("Task updated.");
                return;
            }
        }
        System.out.println("Task not found.");
    }

    public void deleteTask(int id) {
        List<Task> tasks = loadAll();
        if (tasks.removeIf(t -> t.getId() == id)) {
            saveAll(tasks);
            System.out.println("Deleted.");
        } else
            System.out.println("Not found.");
    }

    public void markStatus(int id, TaskStatus status) {
        List<Task> tasks = loadAll();
        for (Task t : tasks) {
            if (t.getId() == id) {
                t.setStatus(status);
                try {
                    java.lang.reflect.Field fUpdate = t.getClass().getDeclaredField("updatedAt");
                    fUpdate.setAccessible(true);
                    fUpdate.set(t, LocalDateTime.now().toString());
                } catch (Exception e) {
                }
                saveAll(tasks);
                System.out.println("Task marked as " + status);
                return;
            }
        }
        System.out.println("Not found.");
    }
}
