package com.roadmap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class ExpenseTrackerApp extends Application {
    private ExpenseManager manager;
    private ObservableList<Expense> expenseList;
    private TableView<Expense> tableView;
    private Label totalLabel;

    @Override
    public void start(Stage primaryStage) {
        manager = new ExpenseManager("expenses.json");

        primaryStage.setTitle("Expense Tracker");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(createInputSection());
        root.setCenter(createTableSection());
        root.setBottom(createSummarySection());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshTable();
    }

    private VBox createInputSection() {
        VBox inputBox = new VBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        HBox row1 = new HBox(10);
        Label descLabel = new Label("Description:");
        TextField descField = new TextField();
        descField.setPromptText("Enter description");
        descField.setId("descriptionField");
        row1.getChildren().addAll(descLabel, descField);

        HBox row2 = new HBox(10);
        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setId("amountField");
        Label catLabel = new Label("Category:");
        TextField catField = new TextField();
        catField.setPromptText("Optional category");
        catField.setId("categoryField");
        row2.getChildren().addAll(amountLabel, amountField, catLabel, catField);

        HBox row3 = new HBox(10);
        Button addBtn = new Button("Add Expense");
        addBtn.setId("addButton");
        Button updateBtn = new Button("Update Selected");
        updateBtn.setId("updateButton");
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setId("deleteButton");
        Button exportBtn = new Button("Export CSV");
        exportBtn.setId("exportButton");
        row3.getChildren().addAll(addBtn, updateBtn, deleteBtn, exportBtn);

        addBtn.setOnAction(e -> {
            String desc = descField.getText();
            String amtStr = amountField.getText();
            String cat = catField.getText();

            if (desc.isEmpty() || amtStr.isEmpty()) {
                showAlert("Error", "Description and amount are required.");
                return;
            }

            try {
                double amount = Double.parseDouble(amtStr);
                String category = cat.isEmpty() ? null : cat;
                int id = manager.addExpense(desc, amount, category);
                showAlert("Success", "Expense added (ID: " + id + ")");
                descField.clear();
                amountField.clear();
                catField.clear();
                refreshTable();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid amount format.");
            } catch (IllegalArgumentException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            Expense selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Select an expense to update.");
                return;
            }

            String desc = descField.getText();
            String amtStr = amountField.getText();
            String cat = catField.getText();

            try {
                double amount = amtStr.isEmpty() ? -1 : Double.parseDouble(amtStr);
                String description = desc.isEmpty() ? null : desc;
                String category = cat.isEmpty() ? null : cat;
                manager.updateExpense(selected.getId(), description, amount, category);
                showAlert("Success", "Expense updated.");
                descField.clear();
                amountField.clear();
                catField.clear();
                refreshTable();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid amount format.");
            } catch (IllegalArgumentException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Expense selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Select an expense to delete.");
                return;
            }
            manager.deleteExpense(selected.getId());
            showAlert("Success", "Expense deleted.");
            refreshTable();
        });

        exportBtn.setOnAction(e -> {
            manager.exportToCsv("expenses_export.csv");
            showAlert("Success", "Expenses exported to expenses_export.csv");
        });

        inputBox.getChildren().addAll(row1, row2, row3);
        return inputBox;
    }

    private VBox createTableSection() {
        VBox tableBox = new VBox(5);

        HBox filterRow = new HBox(10);
        Label filterLabel = new Label("Filter by category:");
        TextField filterField = new TextField();
        filterField.setPromptText("Category name");
        filterField.setId("filterField");
        Button filterBtn = new Button("Filter");
        filterBtn.setId("filterButton");
        Button clearFilterBtn = new Button("Show All");
        clearFilterBtn.setId("clearFilterButton");

        filterBtn.setOnAction(e -> {
            String cat = filterField.getText();
            if (!cat.isEmpty()) {
                List<Expense> filtered = manager.listExpensesByCategory(cat);
                expenseList.setAll(filtered);
            }
        });

        clearFilterBtn.setOnAction(e -> {
            filterField.clear();
            refreshTable();
        });

        filterRow.getChildren().addAll(filterLabel, filterField, filterBtn, clearFilterBtn);

        tableView = new TableView<>();
        tableView.setId("expenseTable");

        TableColumn<Expense, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        dateCol.setPrefWidth(100);

        TableColumn<Expense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);

        TableColumn<Expense, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setPrefWidth(120);

        tableView.getColumns().addAll(idCol, dateCol, descCol, amountCol, catCol);

        expenseList = FXCollections.observableArrayList();
        tableView.setItems(expenseList);

        tableBox.getChildren().addAll(filterRow, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        return tableBox;
    }

    private HBox createSummarySection() {
        HBox summaryBox = new HBox(20);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");

        totalLabel = new Label("Total: $0.00");
        totalLabel.setId("totalLabel");

        Label monthLabel = new Label("Month summary:");
        TextField monthField = new TextField();
        monthField.setPromptText("1-12");
        monthField.setId("monthField");
        monthField.setMaxWidth(50);
        Button monthBtn = new Button("Get Monthly Total");
        monthBtn.setId("monthSummaryButton");

        Label monthResult = new Label("");
        monthBtn.setOnAction(e -> {
            String mStr = monthField.getText();
            if (mStr.isEmpty()) {
                showAlert("Error", "Enter a month (1-12).");
                return;
            }
            try {
                int m = Integer.parseInt(mStr);
                if (m < 1 || m > 12) {
                    showAlert("Error", "Month must be 1-12.");
                    return;
                }
                double total = manager.getMonthlyTotal(m);
                monthResult.setText(String.format("%s: $%.2f", Month.of(m), total));
            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid month.");
            }
        });

        summaryBox.getChildren().addAll(totalLabel, monthLabel, monthField, monthBtn, monthResult);
        return summaryBox;
    }

    private void refreshTable() {
        List<Expense> expenses = manager.listExpenses();
        expenseList.setAll(expenses);
        totalLabel.setText(String.format("Total: $%.2f", manager.getTotalExpenses()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
