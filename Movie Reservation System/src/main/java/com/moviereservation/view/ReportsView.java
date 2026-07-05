package com.moviereservation.view;

import com.moviereservation.repository.ReservationRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ReportsView {
    private final ReservationRepository reservationRepo = new ReservationRepository();

    public ScrollPane createView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));

        Label header = new Label("Reports & Analytics");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        try {
            int totalReservations = reservationRepo.getTotalReservations();
            double totalRevenue = reservationRepo.getTotalRevenue();
            int totalCapacity = reservationRepo.getTotalCapacity();
            double utilization = totalCapacity > 0
                ? (double) totalReservations / totalCapacity * 100 : 0;

            HBox statsBox = new HBox(15);
            statsBox.getChildren().addAll(
                createStatCard("Reservations", String.valueOf(totalReservations)),
                createStatCard("Revenue", "$" + String.format("%.2f", totalRevenue)),
                createStatCard("Capacity", String.valueOf(totalCapacity))
            );

            Label utilLabel = new Label(String.format("Capacity Utilization: %.1f%%", utilization));
            utilLabel.setStyle("-fx-font-weight: bold;");

            ProgressBar utilBar = new ProgressBar(utilization / 100);
            utilBar.setPrefWidth(350);

            Label recentHeader = new Label("Recent Reservations");
            recentHeader.setStyle("-fx-font-weight: bold;");

            VBox list = new VBox(4);
            var reservations = reservationRepo.getAllReservations();
            int show = Math.min(reservations.size(), 20);
            for (int i = 0; i < show; i++) {
                var r = reservations.get(i);
                Label item = new Label(r.getMovieTitle() + " | " + r.getShowDate() + " "
                    + r.getShowTime() + " | Seat " + r.getSeatNumber()
                    + " | $" + String.format("%.2f", r.getPrice()));
                list.getChildren().add(item);
            }

            ScrollPane scrollPane = new ScrollPane(list);
            scrollPane.setFitToWidth(true);
            content.getChildren().addAll(header, statsBox, utilLabel, utilBar,
                new Separator(), recentHeader, scrollPane);
        } catch (Exception e) {
            content.getChildren().addAll(header, new Label("Error: " + e.getMessage()));
        }

        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 4;");
        card.setPrefWidth(150);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}
