package com.moviereservation.view;

import com.moviereservation.model.Movie;
import com.moviereservation.model.Showtime;
import com.moviereservation.repository.MovieRepository;
import com.moviereservation.repository.ShowtimeRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ManageShowtimesView {
    private final Runnable refreshCallback;
    private final MovieRepository movieRepo = new MovieRepository();
    private final ShowtimeRepository showtimeRepo = new ShowtimeRepository();

    public ManageShowtimesView(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public ScrollPane createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label header = new Label("Manage Showtimes");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(6);
        form.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10;");

        ComboBox<String> movieBox = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("HH:MM (e.g. 14:30)");
        TextField seatsField = new TextField("100");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        Button addBtn = new Button("Add Showtime");

        form.add(new Label("Movie:"), 0, 0); form.add(movieBox, 1, 0);
        form.add(new Label("Date:"), 0, 1); form.add(datePicker, 1, 1);
        form.add(new Label("Time:"), 0, 2); form.add(timeField, 1, 2);
        form.add(new Label("Seats:"), 0, 3); form.add(seatsField, 1, 3);
        form.add(new Label("Price:"), 0, 4); form.add(priceField, 1, 4);
        form.add(addBtn, 1, 5);

        try {
            List<Movie> movies = movieRepo.getAllMovies();
            for (Movie m : movies) {
                movieBox.getItems().add(m.getId() + " - " + m.getTitle());
            }
            if (!movieBox.getItems().isEmpty()) movieBox.setValue(movieBox.getItems().get(0));
        } catch (Exception e) { /* skip */ }

        addBtn.setOnAction(e -> {
            try {
                String selected = movieBox.getValue();
                if (selected == null) {
                    new Alert(Alert.AlertType.WARNING, "Select a movie.").showAndWait();
                    return;
                }
                int movieId = Integer.parseInt(selected.split(" - ")[0]);
                LocalTime time = LocalTime.parse(timeField.getText().trim());
                int seats = Integer.parseInt(seatsField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                showtimeRepo.addShowtime(movieId, datePicker.getValue(), time, seats, price);
                new Alert(Alert.AlertType.INFORMATION, "Showtime added!").showAndWait();
                refreshCallback.run();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid number format.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        VBox list = new VBox(6);
        try {
            List<Showtime> showtimes = showtimeRepo.getShowtimesForDate(LocalDate.now());
            for (Showtime st : showtimes) {
                HBox card = new HBox(10);
                card.setPadding(new Insets(6));
                card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

                Label info = new Label(st.getMovieTitle() + " | " + st.getShowDate() + " "
                    + st.getShowTime() + " | Seats: " + st.getTotalSeats()
                    + " | $" + String.format("%.2f", st.getPrice()));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button deleteBtn = new Button("Delete");
                deleteBtn.setOnAction(e -> {
                    try {
                        showtimeRepo.deleteShowtime(st.getId());
                        refreshCallback.run();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                    }
                });

                card.getChildren().addAll(info, spacer, deleteBtn);
                list.getChildren().add(card);
            }
        } catch (Exception e) {
            list.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        content.getChildren().addAll(header, form, new Separator(), scrollPane);
        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }
}
