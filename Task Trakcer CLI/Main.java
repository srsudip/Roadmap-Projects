// Main.java
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: task-cli <command> [arguments]");
            return;
        }

        TaskManager manager = new TaskManager();
        String command = args[0];

        try {
            switch (command) {
                case "add":
                    manager.addTask(args[1]);
                    break;
                case "update":
                    manager.updateTask(Integer.parseInt(args[1]), args[2]);
                    break;
                case "delete":
                    manager.deleteTask(Integer.parseInt(args[1]));
                    break;
                case "list":
                    String filter = (args.length > 1) ? args[1] : null;
                    manager.listTasks(filter);
                    break;
                case "mark-in-progress":
                    manager.markStatus(Integer.parseInt(args[1]), TaskStatus.in_progress);
                    break;
                case "mark-done":
                    manager.markStatus(Integer.parseInt(args[1]), TaskStatus.done);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Missing arguments for command: " + command);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
