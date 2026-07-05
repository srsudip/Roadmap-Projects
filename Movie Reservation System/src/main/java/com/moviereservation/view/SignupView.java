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

public class SignupView {
    private final Stage stage;
    private final UserRepository userRepo;
    private final Consumer<User> onSignupSuccess;

    public SignupView(Stage stage, UserRepository userRepo, Consumer<User> onSignupSuccess) {
        this.stage = stage;
        this.userRepo = userRepo;
        this.onSignupSuccess = onSignupSuccess;
    }

    public Scene createScene() {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setMaxWidth(250);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 chars)");
        passwordField.setMaxWidth(250);

        Button signupBtn = new Button("Sign Up");
        signupBtn.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Label successLabel = new Label();

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
                if (userRepo.signup(username, password, fullName)) {
                    User user = userRepo.login(username, password);
                    if (user != null) onSignupSuccess.accept(user);
                } else {
                    errorLabel.setText("Username already taken");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setOnAction(e -> stage.setScene(new LoginView(stage, userRepo, onSignupSuccess).createScene()));

        root.getChildren().addAll(title, fullNameField, usernameField, passwordField,
                signupBtn, errorLabel, successLabel, loginLink);
        return new Scene(root, 400, 400);
    }
}
