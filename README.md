# Task Tracker CLI (Java)

A lightweight Command Line Interface (CLI) application used to manage tasks and to-do lists. This project was built as part of the **[roadmap.sh Task Tracker challenge](https://roadmap.sh/projects/task-tracker)**.

## 📝 Project Description
This application allows users to track what needs to be done, what is currently in progress, and what has been completed. It provides a simple terminal interface to manage tasks while ensuring data persistence using a local JSON file.

**Challenge Link:** [https://roadmap.sh/projects/task-tracker](https://roadmap.sh/projects/task-tracker)

## ✨ Features
- **Add Tasks:** Create new tasks with unique IDs and timestamps.
- **Update Tasks:** Modify the description of existing tasks.
- **Manage Status:** Easily mark tasks as `todo`, `in-progress`, or `done`.
- **Delete Tasks:** Remove tasks from your list.
- **Filter & List:** View all tasks or filter by specific statuses (`todo`, `in-progress`, `done`).
- **Data Persistence:** All data is saved to a local `tasks.json` file using native Java File I/O.

## 🛠 Technical Implementation Details
To demonstrate fundamental programming skills, this project adheres to the following constraints:
- **No External Libraries:** No JSON parsing libraries (like Jackson or Gson) were used. I implemented a custom JSON serializer and parser using `java.util.regex` and String manipulation.
- **Language:** Java (Standard Edition).
- **Data Format:** Standard JSON structure for portability and readability.

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 11** or higher must be installed on your system.

### Compilation
To compile the application, run the following command in your terminal:
```bash
javac TaskStatus.java Task.java TaskManager.java Main.java
```

### Usage Examples
Once compiled, you can use the following commands:

| Action | Command Example |
| :--- | :--- |
| **Add a task** | `java Main add "Buy groceries"` |
| **Update task** | `java Main update 1 "Buy milk and bread"` |
| **Mark as in-progress** | `java Main mark-in-progress 1` |
| **Mark as done** | `java Main mark-done 1` |
| **Delete a task** | `java Main delete 1` |
| **List all tasks** | `java Main list` |
| **Filter by status** | `java Main list done` |

## 📂 Project Structure
```text
task-tracker/
├── Main.java           # Entry point & command argument parsing
├── TaskManager.java    # Core logic for file I/O and task manipulation
├── Task.java           # Data model for a Task object
├── TaskStatus.java     # Enum representing the three task states
└── tasks.json          # Local storage file (auto-generated)
```

---
