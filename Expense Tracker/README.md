# Expense Tracker (Java)

A CLI application to manage personal finances — add, delete, update, and view expenses with summaries and budget tracking. Built as part of the **[roadmap.sh Expense Tracker challenge](https://roadmap.sh/projects/expense-tracker)**.

## 📝 Project Description
This application allows users to track expenses via the command line, providing CRUD operations, monthly summaries, category filtering, budget alerts, and CSV export — all with JSON persistence.

**Challenge Link:** [https://roadmap.sh/projects/expense-tracker](https://roadmap.sh/projects/expense-tracker)

## ✨ Features
- **Add Expenses:** Create expenses with description, amount, and optional category.
- **Update Expenses:** Modify existing expense details.
- **Delete Expenses:** Remove expenses by ID.
- **List Expenses:** View all expenses or filter by category.
- **Summaries:** View total expenses, monthly totals, or category totals.
- **Budget Tracking:** Set monthly budgets with warnings when exceeded.
- **CSV Export:** Export all expenses to a CSV file.
- **Data Persistence:** All data saved to `expenses.json`.

## 🛠 Technical Implementation
- **Language:** Java (JDK 11+)
- **No External Libraries:** Custom JSON serialization/parsing using String manipulation.
- **Data Format:** JSON for portability and readability.

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 11** or higher

### Compilation
```bash
javac Expense.java ExpenseManager.java Main.java
```

### Usage Examples

| Action | Command |
| :--- | :--- |
| **Add expense** | `java Main add --description "Lunch" --amount 20` |
| **Add with category** | `java Main add --description "Groceries" --amount 50 --category Food` |
| **List all** | `java Main list` |
| **Filter by category** | `java Main list --category Food` |
| **View summary** | `java Main summary` |
| **Monthly summary** | `java Main summary --month 8` |
| **Category summary** | `java Main summary --category Food` |
| **Update expense** | `java Main update --id 1 --amount 25` |
| **Delete expense** | `java Main delete --id 2` |
| **Set budget** | `java Main budget --month 8 --amount 500` |
| **Export to CSV** | `java Main export --path expenses.csv` |

## 📂 Project Structure
```text
expense-tracker/
├── Main.java           # Entry point & CLI argument parsing
├── ExpenseManager.java # Core logic for CRUD, persistence & summaries
├── Expense.java        # Data model for an Expense object
└── expenses.json       # Local storage (auto-generated)
```
