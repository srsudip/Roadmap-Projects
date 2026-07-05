package com.moviereservation.view;

import com.moviereservation.model.Reservation;
import com.moviereservation.model.User;
import com.moviereservation.repository.ReservationRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class MyReservationsView {
    private final User currentUser;
    private final Runnable refreshCallback;
    private final ReservationRepository reservationRepo = new ReservationRepository();

    public MyReservationsView(User currentUser, Runnable refreshCallback) {
        this.currentUser = currentUser;
        this.refreshCallback = refreshCallback;
    }

    public ScrollPane createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label header = new Label("My Reservations");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox list = new VBox(6);

        try {
            List<Reservation> reservations = reservationRepo.getUserReservations(currentUser.getId());
            if (reservations.isEmpty()) {
                list.getChildren().add(new Label("No upcoming reservations."));
            }
            for (Reservation r : reservations) {
                HBox card = new HBox(10);
                card.setPadding(new Insets(6));
                card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

                Label info = new Label(r.getMovieTitle() + " | " + r.getShowDate() + " "
                    + r.getShowTime() + " | Seat " + r.getSeatNumber()
                    + " | $" + String.format("%.2f", r.getPrice()));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button cancelBtn = new Button("Cancel");
                cancelBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this reservation?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                reservationRepo.cancelReservation(r.getId(), currentUser.getId());
                                refreshCallback.run();
                            } catch (Exception ex) {
                                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                            }
                        }
                    });
                });

                card.getChildren().addAll(info, spacer, cancelBtn);
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
