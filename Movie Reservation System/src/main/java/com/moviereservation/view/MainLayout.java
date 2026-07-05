package com.moviereservation.view;

import com.moviereservation.model.User;
import com.moviereservation.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class MainLayout {
    private final Stage stage;
    private User currentUser;
    private final BorderPane root;

    public MainLayout(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.root = new BorderPane();
    }

    public Scene createScene() {
        HBox navbar = new HBox(15);
        navbar.setPadding(new Insets(10));
        navbar.setStyle("-fx-border-color: gray; -fx-border-width: 0 0 1 0;");

        Label titleLabel = new Label("Movie Reservation System");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> showLogin());

        navbar.getChildren().addAll(titleLabel, spacer, userLabel, logoutBtn);

        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(5));
        sidebar.setPrefWidth(180);
        sidebar.setStyle("-fx-border-color: gray; -fx-border-width: 0 1 0 0;");

        Button browseBtn = new Button("Browse Movies");
        browseBtn.setMaxWidth(Double.MAX_VALUE);
        browseBtn.setOnAction(e -> showBrowseMovies());

        Button myResBtn = new Button("My Reservations");
        myResBtn.setMaxWidth(Double.MAX_VALUE);
        myResBtn.setOnAction(e -> showMyReservations());

        sidebar.getChildren().addAll(browseBtn, myResBtn);

        if (currentUser.isAdmin()) {
            Label adminLabel = new Label("ADMIN");
            adminLabel.setStyle("-fx-font-weight: bold;");

            Button manageMovies = new Button("Manage Movies");
            manageMovies.setMaxWidth(Double.MAX_VALUE);
            manageMovies.setOnAction(e -> showManageMovies());

            Button manageShowtimes = new Button("Manage Showtimes");
            manageShowtimes.setMaxWidth(Double.MAX_VALUE);
            manageShowtimes.setOnAction(e -> showManageShowtimes());

            Button reports = new Button("Reports");
            reports.setMaxWidth(Double.MAX_VALUE);
            reports.setOnAction(e -> showReports());

            Button users = new Button("Users");
            users.setMaxWidth(Double.MAX_VALUE);
            users.setOnAction(e -> showUsers());

            sidebar.getChildren().addAll(new Separator(), adminLabel,
                manageMovies, manageShowtimes, reports, users);
        }

        root.setTop(navbar);
        root.setLeft(sidebar);
        showBrowseMovies();

        return new Scene(root, 950, 650);
    }

    private Consumer<User> loginHandler() {
        return user -> {
            this.currentUser = user;
            stage.setScene(createScene());
        };
    }

    private void showLogin() {
        stage.setScene(new LoginView(stage, new UserRepository(), loginHandler()).createScene());
    }

    private void showBrowseMovies() {
        root.setCenter(new BrowseMoviesView(currentUser).createView());
    }

    private void showMyReservations() {
        root.setCenter(new MyReservationsView(currentUser, this::showMyReservations).createView());
    }

    private void showManageMovies() {
        root.setCenter(new ManageMoviesView(this::showManageMovies).createView());
    }

    private void showManageShowtimes() {
        root.setCenter(new ManageShowtimesView(this::showManageShowtimes).createView());
    }

    private void showReports() {
        root.setCenter(new ReportsView().createView());
    }

    private void showUsers() {
        root.setCenter(new UsersView(currentUser.getId(), this::showUsers).createView());
    }
}
