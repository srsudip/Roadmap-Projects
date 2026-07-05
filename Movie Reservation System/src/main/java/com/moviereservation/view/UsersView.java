package com.moviereservation.view;

import com.moviereservation.model.User;
import com.moviereservation.repository.UserRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class UsersView {
    private final int currentUserId;
    private final Runnable refreshCallback;
    private final UserRepository userRepo = new UserRepository();

    public UsersView(Runnable refreshCallback) {
        this.currentUserId = -1;
        this.refreshCallback = refreshCallback;
    }

    public UsersView(int currentUserId, Runnable refreshCallback) {
        this.currentUserId = currentUserId;
        this.refreshCallback = refreshCallback;
    }

    public ScrollPane createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label header = new Label("User Management");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox list = new VBox(6);

        try {
            List<User> users = userRepo.getAllUsers();
            for (User u : users) {
                HBox card = new HBox(10);
                card.setPadding(new Insets(6));
                card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

                Label info = new Label(u.getFullName() + " (@" + u.getUsername() + ") - " + u.getRole());
                if ("ADMIN".equals(u.getRole())) info.setStyle("-fx-font-weight: bold;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                if (u.getId() != currentUserId) {
                    if ("USER".equals(u.getRole())) {
                        Button promoteBtn = new Button("Promote to Admin");
                        promoteBtn.setOnAction(e -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "Promote " + u.getFullName() + " to Admin?", ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.YES) {
                                    try {
                                        userRepo.promoteUser(u.getId());
                                        new Alert(Alert.AlertType.INFORMATION, u.getFullName() + " is now an Admin.").showAndWait();
                                        refreshCallback.run();
                                    } catch (Exception ex) {
                                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                                    }
                                }
                            });
                        });
                        card.getChildren().addAll(info, spacer, promoteBtn);
                    } else {
                        Button demoteBtn = new Button("Demote to User");
                        demoteBtn.setOnAction(e -> {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                "Demote " + u.getFullName() + "?", ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.YES) {
                                    try {
                                        userRepo.demoteUser(u.getId());
                                        new Alert(Alert.AlertType.INFORMATION, u.getFullName() + " is now a regular user.").showAndWait();
                                        refreshCallback.run();
                                    } catch (Exception ex) {
                                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                                    }
                                }
                            });
                        });
                        card.getChildren().addAll(info, spacer, demoteBtn);
                    }
                } else {
                    card.getChildren().addAll(info, spacer, new Label("(You)"));
                }

                list.getChildren().add(card);
            }
        } catch (Exception e) {
            list.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        content.getChildren().addAll(header, scrollPane);
        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }
}
