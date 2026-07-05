# Task Tracker CLI

A lightweight, zero-dependency Command Line Interface (CLI) application written in Java for managing your daily tasks. This project was built to demonstrate core Java programming skills, including file I/O, manual JSON serialization, regular expressions, and command-line argument parsing.

## 🚀 Features

Manage your productivity directly from the terminal:
- **Add Tasks:** Quickly create new tasks with a unique ID.
- **Update Tasks:** Modify task descriptions at any time.
- **Track Progress:** Easily move tasks between `todo`, `in-progress`, and `done` statuses.
- **Delete Tasks:** Clean up your list by removing completed or unwanted tasks.
- **Smart Listing:** View all tasks or filter them by their specific status.
- **Local Persistence:** All data is automatically saved to a local `tasks.json` file.

## 🛠 Technical Constraints (The "No Libraries" Challenge)

To maximize learning, this project adheres to the following constraints:
- **Language:** Java (Standard Edition).
- **Zero External Dependencies:** No Maven/Gradle dependencies or JSON libraries were used.
- **Manual Serialization:** Implemented a custom logic to convert Java objects into JSON strings using `StringBuilder`.
- **Regex Parsing:** Utilized `java.util.regex` to parse and reconstruct data from the filesystem.

## 📋 Prerequisites

To run this application, you must have:
- **Java Development Kit (JDK) 11** or higher installed on your system.

---

## 🚀 Getting Started

### 1. Clone the Project
```bash
git clone <your-repo-link>
cd task-tracker
```

### 2. Compile the Application
Open your terminal in the project directory and run:
```bash
javac TaskStatus.java Task.java TaskManager.java Main.java
```

### 3. Usage Guide

The application is executed using `java Main <command> [arguments]`.

| Action | Command Example | Description |
| :--- | :--- | :--- |
| **Add a task** | `java Main add "Buy groceries"` | Creates a new `todo` task. |
| **Update task** | `java Main update 1 "New description"` | Changes the text of an existing task ID. |
| **Delete task** | `java Main delete 1` | Removes task with specified ID. |
| **Start working** | `java Main mark-in-progress 1` | Sets status to `in-progress`. |
| **Mark as done** | `java Main mark-done 1` | Sets status to `done`. |
| **List all tasks** | `java Main list` | Displays everything in the list. |
| **Filter by status**| `java Main list done` | Shows only `done` (or `todo`/`in-progress`) tasks. |

---

## 📂 Project Structure

```text
task-tracker/
├── Main.java           # CLI entry point and command routing
├── TaskManager.java    # Logic for file I/O and data manipulation
├── Task.java           # The Task Data Model
├── TaskStatus.java     # Enum representing task states
└── tasks.json          # Local storage (generated automatically)
```

## 🛡 Error Handling

The application handles several edge cases:
- **Missing Arguments:** Provides usage instructions if a command is called incorrectly.
- **Invalid IDs:** Gracefully handles attempts to update or delete non-existent IDs.
- **File Management:** Automatically creates `tasks.json` if it doesn't exist and prevents data loss by reading the file before every modification.

---
