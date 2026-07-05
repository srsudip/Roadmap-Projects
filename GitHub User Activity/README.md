# GitHub User Activity (Java)

This project is a CLI application that fetches and displays recent activity of any GitHub user via the GitHub API. It was built as part of the [roadmap.sh Task Tracker Project](https://roadmap.sh/projects/github-user-activity).

## 🚀 Features
- **Fetch User Activity:** Connects to the GitHub API to retrieve real-time events.
- **Console Output:** Displays a human-readable summary of commits, issues, stars, and more.
- **Zero Dependencies:** Built using standard Java libraries (no external JSON or HTTP libraries used).
- **Error Handling:** Gracefully handles invalid usernames and network failures.

## 🛠 Requirements & Constraints
- **Language:** Java (JDK 11+)
- **API Usage:** Uses the official GitHub Events API.
- **No External Libraries:** Implemented manual JSON parsing using Regular Expressions to satisfy project constraints.

## 💻 Installation & Execution

### Prerequisites
Ensure you have [Java JDK](https://www.oracle.com/java/technologies/downloads/) installed and added to your system's PATH.

### 1. Compilation
Open your terminal in the project directory and run:
```bash
javac GitHubActivityApp.java
```

### 2. Running the Application
Provide a GitHub username as an argument:
```bash
java GitHubActivityApp <username>
```

**Example Output:**
```text
Fetching activity for google...

- Pushed 3 commit(s) to open-source-repo
- Starred google/awesome-list
- Created a new branch in google/some-project
```

## 🔗 Project Link
This project was completed following the requirements at: [https://roadmap.sh/projects/github-user-activity](https://roadmap.sh/projects/github-user-activity)
