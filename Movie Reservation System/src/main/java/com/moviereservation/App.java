package com.moviereservation;

import com.moviereservation.db.DatabaseManager;
import com.moviereservation.model.User;
import com.moviereservation.repository.UserRepository;
import com.moviereservation.view.LoginView;
import com.moviereservation.view.MainLayout;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseManager.getInstance();
        stage.setTitle("Movie Reservation System");
        stage.setScene(new LoginView(stage, new UserRepository(), user -> {
            stage.setScene(new MainLayout(stage, user).createScene());
        }).createScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
