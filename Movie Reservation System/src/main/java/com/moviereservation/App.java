package com.moviereservation;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private Database db;
    private Database.User currentUser;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        db = new Database();
        stage.setTitle("Movie Reservation System");
        showLoginView();
        stage.show();
    }

    // ======================== LOGIN / SIGNUP ========================

    private void showLoginView() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("\uD83C\uDFAC Movie Reservation System");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Sign in to continue");
        subtitle.setTextFill(Color.LIGHTGRAY);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button loginBtn = createButton("Login", "#e94560");
        loginBtn.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                return;
            }
            try {
                currentUser = db.login(username, password);
                if (currentUser != null) {
                    showMainView();
                } else {
                    errorLabel.setText("Invalid username or password");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign up");
        signupLink.setTextFill(Color.LIGHTBLUE);
        signupLink.setOnAction(e -> showSignupView());

        root.getChildren().addAll(title, subtitle, usernameField, passwordField,
                loginBtn, errorLabel, signupLink);
        primaryStage.setScene(new Scene(root, 500, 450));
    }

    private void showSignupView() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("Create Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setMaxWidth(300);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        Button signupBtn = createButton("Sign Up", "#e94560");
        signupBtn.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Label successLabel = new Label();
        successLabel.setTextFill(Color.LIGHTGREEN);

        signupBtn.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                return;
            }
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
                return;
            }
            try {
                if (db.signup(username, password, fullName)) {
                    successLabel.setText("Account created! You can now login.");
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Username already taken");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setTextFill(Color.LIGHTBLUE);
        loginLink.setOnAction(e -> showLoginView());

        root.getChildren().addAll(title, fullNameField, usernameField, passwordField,
                signupBtn, errorLabel, successLabel, loginLink);
        primaryStage.setScene(new Scene(root, 500, 500));
    }

    // ======================== MAIN VIEW ========================

    private void showMainView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #16213e;");

        // Top nav
        HBox navbar = new HBox(20);
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(15));
        navbar.setStyle("-fx-background-color: #0f3460;");
        navbar.getChildren().add(createNavLabel("\uD83C\uDFAC Movie Reservation"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("\uD83D\uDC64 " + currentUser.fullName + " (" + currentUser.role + ")");
        userLabel.setTextFill(Color.LIGHTGRAY);

        Button logoutBtn = createSmallButton("Logout", "#e94560");
        logoutBtn.setOnAction(e -> {
            currentUser = null;
            showLoginView();
        });

        navbar.getChildren().addAll(spacer, userLabel, logoutBtn);

        // Left sidebar
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #0f3460; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(200);

        Button browseBtn = createSidebarButton("\uD83C\uDFAC Browse Movies");
        browseBtn.setOnAction(e -> showBrowseMovies(root));

        Button myResBtn = createSidebarButton("\uD83C\uDF9F My Reservations");
        myResBtn.setOnAction(e -> showMyReservations(root));

        sidebar.getChildren().addAll(browseBtn, myResBtn);

        if (currentUser.isAdmin()) {
            Label adminLabel = new Label("  ADMIN");
            adminLabel.setTextFill(Color.GOLD);
            adminLabel.setFont(Font.font("System", FontWeight.BOLD, 11));

            Button moviesBtn = createSidebarButton("\uD83C\uDFAC Manage Movies");
            moviesBtn.setOnAction(e -> showManageMovies(root));

            Button showtimesBtn = createSidebarButton("\uD83D\uDD50 Manage Showtimes");
            showtimesBtn.setOnAction(e -> showManageShowtimes(root));

            Button reportsBtn = createSidebarButton("\uD83D\uDCCA Reports");
            reportsBtn.setOnAction(e -> showReports(root));

            Button usersBtn = createSidebarButton("\uD83D\uDC65 Users");
            usersBtn.setOnAction(e -> showUsers(root));

            sidebar.getChildren().addAll(new Separator(), adminLabel, moviesBtn, showtimesBtn, reportsBtn, usersBtn);
        }

        root.setTop(navbar);
        root.setLeft(sidebar);

        showBrowseMovies(root);

        primaryStage.setScene(new Scene(root, 1000, 700));
    }

    // ======================== BROWSE MOVIES ========================

    private void showBrowseMovies(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("Browse Movies");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label("Select Date:");
        dateLabel.setTextFill(Color.LIGHTGRAY);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        Button refreshBtn = createButton("Show Times", "#e94560");
        dateBox.getChildren().addAll(dateLabel, datePicker, refreshBtn);

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        Label genreLabel = new Label("Filter by Genre:");
        genreLabel.setTextFill(Color.LIGHTGRAY);
        ComboBox<String> genreFilter = new ComboBox<>();
        genreFilter.getItems().addAll("All", "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Crime", "Thriller");
        genreFilter.setValue("All");
        filterBox.getChildren().addAll(genreLabel, genreFilter);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox movieList = new VBox(10);
        movieList.setPadding(new Insets(5));

        Runnable loadMovies = () -> {
            movieList.getChildren().clear();
            try {
                LocalDate selectedDate = datePicker.getValue();
                List<Database.Showtime> showtimes = db.getShowtimesForDate(selectedDate);
                String genre = genreFilter.getValue();

                if (showtimes.isEmpty()) {
                    Label empty = new Label("No showtimes available for this date.");
                    empty.setTextFill(Color.LIGHTGRAY);
                    movieList.getChildren().add(empty);
                    return;
                }

                for (Database.Showtime st : showtimes) {
                    if (!"All".equals(genre) && !st.genre.equals(genre)) continue;
                    int available = db.getAvailableSeats(st.id);
                    HBox card = createShowtimeCard(st, available);
                    movieList.getChildren().add(card);
                }
            } catch (Exception e) {
                Label err = new Label("Error loading data: " + e.getMessage());
                err.setTextFill(Color.RED);
                movieList.getChildren().add(err);
            }
        };

        refreshBtn.setOnAction(e -> loadMovies.run());
        genreFilter.setOnAction(e -> loadMovies.run());
        loadMovies.run();

        scrollPane.setContent(movieList);
        content.getChildren().addAll(header, dateBox, filterBox, scrollPane);
        root.setCenter(content);
    }

    private HBox createShowtimeCard(Database.Showtime st, int available) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        Label titleLabel = new Label(st.movieTitle);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);

        Label genreInfo = new Label(st.genre + " \u2022 " + st.durationMinutes + " min");
        genreInfo.setTextFill(Color.LIGHTGRAY);

        Label timeLabel = new Label("\u23F0 " + st.showTime + " \u2022 \uD83E\uDEE1 " + available + " seats available");
        timeLabel.setTextFill(available > 10 ? Color.LIGHTGREEN : Color.ORANGE);

        Label priceLabel = new Label("$" + String.format("%.2f", st.price));
        priceLabel.setTextFill(Color.LIGHTGREEN);
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        info.getChildren().addAll(titleLabel, genreInfo, timeLabel, priceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button reserveBtn = createButton("Reserve Seats", "#e94560");
        reserveBtn.setOnAction(e -> showSeatSelection(st));
        reserveBtn.setDisable(available <= 0);

        card.getChildren().addAll(info, spacer, reserveBtn);
        return card;
    }

    // ======================== SEAT SELECTION ========================

    private void showSeatSelection(Database.Showtime showtime) {
        try {
        Stage dialog = new Stage();
        dialog.setTitle("Select Seats - " + showtime.movieTitle);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #16213e;");

        Label header = new Label(showtime.movieTitle + " - " + showtime.showDate + " " + showtime.showTime);
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.WHITE);

        Label priceLabel = new Label("Price per seat: $" + String.format("%.2f", showtime.price));
        priceLabel.setTextFill(Color.LIGHTGREEN);

        Label screen = new Label("--- SCREEN ---");
        screen.setTextFill(Color.GOLD);
        screen.setFont(Font.font("System", FontWeight.BOLD, 14));
        screen.setAlignment(Pos.CENTER);
        screen.setMaxWidth(Double.MAX_VALUE);

        GridPane seatGrid = new GridPane();
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);
        seatGrid.setPadding(new Insets(10));

        List<Integer> reservedSeats = db.getReservedSeats(showtime.id);
        ToggleButton[][] seats = new ToggleButton[10][10];
        int seatNum = 1;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                int currentSeat = seatNum;
                ToggleButton seatBtn = new ToggleButton(String.valueOf(seatNum));
                seatBtn.setPrefSize(40, 40);

                if (reservedSeats.contains(seatNum)) {
                    seatBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;");
                    seatBtn.setDisable(true);
                    seatBtn.setSelected(true);
                } else {
                    seatBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;");
                }

                seatBtn.setOnAction(e -> {
                    if (seatBtn.isSelected()) {
                        seatBtn.setStyle("-fx-background-color: #4ecca3; -fx-text-fill: black;");
                    } else {
                        seatBtn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;");
                    }
                });

                seats[row][col] = seatBtn;
                seatGrid.add(seatBtn, col, row);
                seatNum++;
            }
        }

        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        Label availableLegend = new Label("Available");
        availableLegend.setTextFill(Color.web("#0f3460"));
        Label selectedLegend = new Label("Selected");
        selectedLegend.setTextFill(Color.web("#4ecca3"));
        Label reservedLegend = new Label("Reserved");
        reservedLegend.setTextFill(Color.web("#e94560"));
        legend.getChildren().addAll(availableLegend, selectedLegend, reservedLegend);

        Label summaryLabel = new Label("Selected: 0 seats | Total: $0.00");
        summaryLabel.setTextFill(Color.WHITE);
        summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Button confirmBtn = createButton("Confirm Reservation", "#4ecca3");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);

        confirmBtn.setOnAction(e -> {
            try {
            List<Integer> selectedSeats = new ArrayList<>();
            for (int r = 0; r < 10; r++) {
                for (int c = 0; c < 10; c++) {
                    if (seats[r][c].isSelected() && !seats[r][c].isDisabled()) {
                        selectedSeats.add((r * 10) + c + 1);
                    }
                }
            }

            if (selectedSeats.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "No seats selected", "Please select at least one seat.");
                return;
            }

            int successCount = 0;
            for (int seat : selectedSeats) {
                if (db.reserveSeat(currentUser.id, showtime.id, seat)) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Reservation Confirmed",
                    successCount + " seat(s) reserved successfully!");
                dialog.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Reservation Failed",
                    "Could not reserve seats. They may have been taken by another user.");
            }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        root.getChildren().addAll(header, priceLabel, screen, seatGrid, legend, summaryLabel, confirmBtn);
        Scene scene = new Scene(root, 520, 620);
        dialog.setScene(scene);
        dialog.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // ======================== MY RESERVATIONS ========================

    private void showMyReservations(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("My Reservations");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox list = new VBox(10);

        try {
            List<Database.Reservation> reservations = db.getUserReservations(currentUser.id);
            if (reservations.isEmpty()) {
                Label empty = new Label("No reservations yet. Browse movies to book seats!");
                empty.setTextFill(Color.LIGHTGRAY);
                list.getChildren().add(empty);
            }
            for (Database.Reservation r : reservations) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(12));
                card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
                card.setAlignment(Pos.CENTER_LEFT);

                VBox info = new VBox(4);
                Label titleLabel = new Label(r.movieTitle);
                titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                titleLabel.setTextFill(Color.WHITE);

                Label detailLabel = new Label(
                    "\uD83D\uDCC5 " + r.showDate + " \u23F0 " + r.showTime + " \uD83E\uDEE1 Seat " + r.seatNumber);
                detailLabel.setTextFill(Color.LIGHTGRAY);

                Label priceLabel = new Label("$" + String.format("%.2f", r.price));
                priceLabel.setTextFill(Color.LIGHTGREEN);

                info.getChildren().addAll(titleLabel, detailLabel, priceLabel);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button cancelBtn = createSmallButton("Cancel", "#e94560");
                final int reservationId = r.id;
                cancelBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this reservation?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                db.cancelReservation(reservationId, currentUser.id);
                                showMyReservations(root);
                            } catch (Exception ex) {
                                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                            }
                        }
                    });
                });

                card.getChildren().addAll(info, spacer, cancelBtn);
                list.getChildren().add(card);
            }
        } catch (Exception e) {
            Label err = new Label("Error: " + e.getMessage());
            err.setTextFill(Color.RED);
            list.getChildren().add(err);
        }

        scrollPane.setContent(list);
        content.getChildren().addAll(header, scrollPane);
        root.setCenter(content);
    }

    // ======================== ADMIN: MANAGE MOVIES ========================

    private void showManageMovies(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("Manage Movies");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-padding: 15;");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(2);
        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.getItems().addAll("Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Crime", "Thriller");
        genreBox.setValue("Action");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (min)");
        TextField posterField = new TextField();
        posterField.setPromptText("Poster URL");

        Button addBtn = createButton("Add Movie", "#4ecca3");

        form.add(new Label("Title:"), 0, 0); form.add(titleField, 1, 0);
        form.add(new Label("Genre:"), 0, 1); form.add(genreBox, 1, 1);
        form.add(new Label("Duration:"), 0, 2); form.add(durationField, 1, 2);
        form.add(new Label("Description:"), 0, 3); form.add(descArea, 1, 3);
        form.add(new Label("Poster URL:"), 0, 4); form.add(posterField, 1, 4);
        form.add(addBtn, 1, 5);

        addBtn.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                String genre = genreBox.getValue();
                int duration = Integer.parseInt(durationField.getText().trim());
                String poster = posterField.getText().trim();

                if (title.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Title is required.");
                    return;
                }

                db.addMovie(title, desc, genre, duration, poster);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Movie added!");
                titleField.clear();
                descArea.clear();
                durationField.clear();
                posterField.clear();
                showManageMovies(root);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Duration must be a number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox movieList = new VBox(10);

        try {
            List<Database.Movie> movies = db.getAllMovies();
            for (Database.Movie m : movies) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(12));
                card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
                card.setAlignment(Pos.CENTER_LEFT);

                VBox info = new VBox(4);
                Label titleLabel = new Label(m.title);
                titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                titleLabel.setTextFill(Color.WHITE);
                Label detail = new Label(m.genre + " \u2022 " + m.durationMinutes + " min");
                detail.setTextFill(Color.LIGHTGRAY);
                info.getChildren().addAll(titleLabel, detail);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button deleteBtn = createSmallButton("Delete", "#e94560");
                deleteBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this movie?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                db.deleteMovie(m.id);
                                showManageMovies(root);
                            } catch (Exception ex) {
                                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                            }
                        }
                    });
                });

                card.getChildren().addAll(info, spacer, deleteBtn);
                movieList.getChildren().add(card);
            }
        } catch (Exception e) {
            movieList.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        scrollPane.setContent(movieList);
        content.getChildren().addAll(header, form, new Separator(), scrollPane);
        root.setCenter(content);
    }

    // ======================== ADMIN: MANAGE SHOWTIMES ========================

    private void showManageShowtimes(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("Manage Showtimes");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8; -fx-padding: 15;");

        ComboBox<String> movieBox = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("HH:MM (e.g., 14:30)");
        TextField seatsField = new TextField();
        seatsField.setText("100");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button addBtn = createButton("Add Showtime", "#4ecca3");

        form.add(new Label("Movie:"), 0, 0); form.add(movieBox, 1, 0);
        form.add(new Label("Date:"), 0, 1); form.add(datePicker, 1, 1);
        form.add(new Label("Time:"), 0, 2); form.add(timeField, 1, 2);
        form.add(new Label("Seats:"), 0, 3); form.add(seatsField, 1, 3);
        form.add(new Label("Price:"), 0, 4); form.add(priceField, 1, 4);
        form.add(addBtn, 1, 5);

        try {
            List<Database.Movie> movies = db.getAllMovies();
            for (Database.Movie m : movies) {
                movieBox.getItems().add(m.id + " - " + m.title);
            }
            if (!movieBox.getItems().isEmpty()) movieBox.setValue(movieBox.getItems().get(0));
        } catch (Exception e) { /* ignore */ }

        addBtn.setOnAction(e -> {
            try {
                String selected = movieBox.getValue();
                if (selected == null) {
                    showAlert(Alert.AlertType.WARNING, "Validation", "Select a movie.");
                    return;
                }
                int movieId = Integer.parseInt(selected.split(" - ")[0]);
                LocalTime time = LocalTime.parse(timeField.getText().trim());
                int seats = Integer.parseInt(seatsField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                db.addShowtime(movieId, datePicker.getValue(), time, seats, price);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Showtime added!");
                showManageShowtimes(root);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid number format.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox list = new VBox(10);

        try {
            List<Database.Showtime> showtimes = db.getShowtimesForDate(LocalDate.now());
            for (Database.Showtime st : showtimes) {
                HBox card = new HBox(15);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
                card.setAlignment(Pos.CENTER_LEFT);

                Label info = new Label(st.movieTitle + " | " + st.showDate + " " + st.showTime
                    + " | Seats: " + st.totalSeats + " | $" + String.format("%.2f", st.price));
                info.setTextFill(Color.WHITE);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button deleteBtn = createSmallButton("Delete", "#e94560");
                deleteBtn.setOnAction(e -> {
                    try {
                        db.deleteShowtime(st.id);
                        showManageShowtimes(root);
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
                    }
                });

                card.getChildren().addAll(info, spacer, deleteBtn);
                list.getChildren().add(card);
            }
        } catch (Exception e) {
            list.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        scrollPane.setContent(list);
        content.getChildren().addAll(header, form, new Separator(), scrollPane);
        root.setCenter(content);
    }

    // ======================== ADMIN: REPORTS ========================

    private void showReports(BorderPane root) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("Reports & Analytics");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        try {
            Database.ReportData report = db.getReportData();

            HBox statsBox = new HBox(20);
            statsBox.setAlignment(Pos.CENTER);

            statsBox.getChildren().add(createStatCard("Reservations",
                String.valueOf(report.totalReservations), "#e94560"));
            statsBox.getChildren().add(createStatCard("Total Revenue",
                "$" + String.format("%.2f", report.totalRevenue), "#4ecca3"));
            statsBox.getChildren().add(createStatCard("Total Capacity",
                String.valueOf(report.totalCapacity), "#0f3460"));

            double utilization = report.totalCapacity > 0
                ? (double) report.totalReservations / report.totalCapacity * 100 : 0;

            Label utilLabel = new Label(String.format("Capacity Utilization: %.1f%%", utilization));
            utilLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            utilLabel.setTextFill(Color.WHITE);

            ProgressBar utilBar = new ProgressBar(utilization / 100);
            utilBar.setPrefWidth(400);
            utilBar.setPrefHeight(25);

            Label recentHeader = new Label("Recent Reservations");
            recentHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
            recentHeader.setTextFill(Color.WHITE);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            VBox list = new VBox(8);

            List<Database.Reservation> reservations = db.getAllReservations();
            int show = Math.min(reservations.size(), 20);
            for (int i = 0; i < show; i++) {
                Database.Reservation r = reservations.get(i);
                Label item = new Label("- " + r.movieTitle + " | " + r.showDate + " " + r.showTime
                    + " | Seat " + r.seatNumber + " | $" + String.format("%.2f", r.price));
                item.setTextFill(Color.LIGHTGRAY);
                list.getChildren().add(item);
            }

            scrollPane.setContent(list);
            content.getChildren().addAll(header, statsBox, utilLabel, utilBar,
                new Separator(), recentHeader, scrollPane);
        } catch (Exception e) {
            content.getChildren().addAll(header, new Label("Error: " + e.getMessage()));
        }

        root.setCenter(content);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.LIGHTGRAY);
        titleLabel.setFont(Font.font("System", 12));

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    // ======================== ADMIN: USERS ========================

    private void showUsers(BorderPane root) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #16213e;");

        Label header = new Label("User Management");
        header.setFont(Font.font("System", FontWeight.BOLD, 22));
        header.setTextFill(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox list = new VBox(10);

        try {
            java.sql.Statement stmt = db.getConnection().createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                "SELECT id, username, full_name, role FROM users ORDER BY id");
            while (rs.next()) {
                int userId = rs.getInt("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String role = rs.getString("role");

                HBox card = new HBox(15);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");
                card.setAlignment(Pos.CENTER_LEFT);

                Label info = new Label("\uD83D\uDC64 " + fullName + " (@" + username + ") - " + role);
                info.setTextFill(Color.WHITE);
                if ("ADMIN".equals(role)) info.setTextFill(Color.GOLD);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                card.getChildren().addAll(info, spacer);
                list.getChildren().add(card);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            list.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        scrollPane.setContent(list);
        content.getChildren().addAll(header, scrollPane);
        root.setCenter(content);
    }

    // ======================== HELPERS ========================

    private Button createButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14; "
            + "-fx-background-radius: 6; -fx-padding: 8 20;");
        return btn;
    }

    private Button createSmallButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12; "
            + "-fx-background-radius: 4; -fx-padding: 4 12;");
        return btn;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; "
            + "-fx-background-radius: 6; -fx-padding: 10 15; -fx-cursor: hand;");
        return btn;
    }

    private Label createNavLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 18));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
