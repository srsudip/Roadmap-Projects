package com.moviereservation.view;

import com.moviereservation.model.User;
import com.moviereservation.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class LoginView {
    private final Stage stage;
    private final UserRepository userRepo;
    private final Consumer<User> onLoginSuccess;

    public LoginView(Stage stage, UserRepository userRepo, Consumer<User> onLoginSuccess) {
        this.stage = stage;
        this.userRepo = userRepo;
        this.onLoginSuccess = onLoginSuccess;
    }

    public Scene createScene() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label title = new Label("Movie Reservation System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign up");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                return;
            }
            try {
                User user = userRepo.login(username, password);
                if (user != null) {
                    onLoginSuccess.accept(user);
                } else {
                    errorLabel.setText("Invalid username or password");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        passwordField.setOnAction(e -> loginBtn.fire());

        signupLink.setOnAction(e -> stage.setScene(new SignupView(stage, userRepo, onLoginSuccess).createScene()));

        root.getChildren().addAll(title, usernameField, passwordField, loginBtn, errorLabel, signupLink);
        return new Scene(root, 400, 350);
    }
}
