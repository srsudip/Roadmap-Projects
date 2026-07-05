package com.moviereservation.view;

import com.moviereservation.model.Movie;
import com.moviereservation.model.Showtime;
import com.moviereservation.model.User;
import com.moviereservation.repository.MovieRepository;
import com.moviereservation.repository.ReservationRepository;
import com.moviereservation.repository.ShowtimeRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BrowseMoviesView {
    private final User currentUser;
    private final MovieRepository movieRepo = new MovieRepository();
    private final ShowtimeRepository showtimeRepo = new ShowtimeRepository();
    private final ReservationRepository reservationRepo = new ReservationRepository();

    public BrowseMoviesView(User currentUser) {
        this.currentUser = currentUser;
    }

    public ScrollPane createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label header = new Label("Browse Movies");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<String> genreFilter = new ComboBox<>();
        genreFilter.getItems().add("All");
        genreFilter.setValue("All");
        try {
            for (Movie m : movieRepo.getAllMovies()) {
                if (!genreFilter.getItems().contains(m.getGenre())) {
                    genreFilter.getItems().add(m.getGenre());
                }
            }
        } catch (Exception ignored) {}
        Button refreshBtn = new Button("Show Times");
        controls.getChildren().addAll(new Label("Date:"), datePicker, new Label("Genre:"), genreFilter, refreshBtn);

        VBox movieList = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(movieList);
        scrollPane.setFitToWidth(true);

        Runnable loadMovies = () -> {
            movieList.getChildren().clear();
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) return;
            String genre = genreFilter.getValue();
            try {
                List<Showtime> showtimes = showtimeRepo.getShowtimesForDate(selectedDate);
                Map<Integer, List<Showtime>> movieShowtimes = new LinkedHashMap<>();
                for (Showtime st : showtimes) {
                    if (!"All".equals(genre) && !st.getGenre().equals(genre)) continue;
                    movieShowtimes.computeIfAbsent(st.getMovieId(), k -> new ArrayList<>()).add(st);
                }
                if (movieShowtimes.isEmpty()) {
                    movieList.getChildren().add(new Label("No showtimes for " + selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + "."));
                    return;
                }
                for (Map.Entry<Integer, List<Showtime>> entry : movieShowtimes.entrySet()) {
                    Movie movie = movieRepo.getMovieById(entry.getKey());
                    if (movie == null) continue;
                    movieList.getChildren().add(createMovieCard(movie, entry.getValue(), selectedDate));
                }
            } catch (Exception e) {
                movieList.getChildren().add(new Label("Error: " + e.getMessage()));
            }
        };

        refreshBtn.setOnAction(e -> loadMovies.run());
        datePicker.setOnAction(e -> loadMovies.run());
        genreFilter.setOnAction(e -> loadMovies.run());
        loadMovies.run();

        content.getChildren().addAll(header, controls, scrollPane);
        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }

    private VBox createMovieCard(Movie movie, List<Showtime> showtimes, LocalDate date) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-radius: 6; -fx-background-color: #fafafa;");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        ImageView poster = new ImageView();
        poster.setFitWidth(80);
        poster.setFitHeight(120);
        poster.setPreserveRatio(true);
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            try {
                poster.setImage(new Image(movie.getPosterUrl()));
            } catch (Exception ignored) {}
        }
        if (poster.getImage() == null) {
            Label noPoster = new Label("[No Poster]");
            noPoster.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");
            noPoster.setMinWidth(80);
            noPoster.setMinHeight(120);
            noPoster.setAlignment(Pos.CENTER);
            poster = null;
            topRow.getChildren().add(noPoster);
        } else {
            topRow.getChildren().add(poster);
        }

        VBox infoBox = new VBox(4);
        Label title = new Label(movie.getTitle());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Label meta = new Label(movie.getGenre() + " | " + movie.getDurationMinutes() + " min");
        meta.setStyle("-fx-text-fill: #555555;");
        Label desc = new Label(movie.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #333333;");
        infoBox.getChildren().addAll(title, meta, desc);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        topRow.getChildren().add(infoBox);

        FlowPane showtimeButtons = new FlowPane(6, 6);
        for (Showtime st : showtimes) {
            int available = 0;
            try { available = reservationRepo.getAvailableSeats(st.getId()); } catch (Exception ignored) {}
            String label = st.getShowTime() + " - " + available + " seats ($" + String.format("%.0f", st.getPrice()) + ")";
            Button btn = new Button(label);
            btn.setDisable(available <= 0);
            btn.setStyle("-fx-font-size: 11px;");
            btn.setOnAction(e -> showSeatSelection(st));
            showtimeButtons.getChildren().add(btn);
        }

        Label showtimeHeader = new Label("Showtimes for " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + ":");
        showtimeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        card.getChildren().addAll(topRow, showtimeHeader, showtimeButtons);
        return card;
    }

    private void showSeatSelection(Showtime showtime) {
        try {
            Stage dialog = new Stage();
            dialog.setTitle("Select Seats - " + showtime.getMovieTitle());

            VBox root = new VBox(10);
            root.setPadding(new Insets(15));

            Label header = new Label(showtime.getMovieTitle() + " - " + showtime.getShowDate() + " " + showtime.getShowTime());
            header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label priceLabel = new Label("Price per seat: $" + String.format("%.2f", showtime.getPrice()));

            Label screen = new Label("--- SCREEN ---");
            screen.setStyle("-fx-font-weight: bold;");

            GridPane seatGrid = new GridPane();
            seatGrid.setAlignment(Pos.CENTER);
            seatGrid.setHgap(3);
            seatGrid.setVgap(3);

            List<Integer> reservedSeats = reservationRepo.getReservedSeats(showtime.getId());
            ToggleButton[][] seats = new ToggleButton[10][10];
            int seatNum = 1;

            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    ToggleButton seatBtn = new ToggleButton(String.valueOf(seatNum));
                    seatBtn.setPrefSize(38, 38);

                    if (reservedSeats.contains(seatNum)) {
                        seatBtn.setDisable(true);
                        seatBtn.setSelected(true);
                        seatBtn.setText("X");
                    }

                    int sn = seatNum;
                    seatBtn.setOnAction(e -> {
                        if (seatBtn.isSelected()) {
                            seatBtn.setStyle("-fx-background-color: lightgreen;");
                        } else {
                            seatBtn.setStyle("");
                        }
                    });

                    seats[row][col] = seatBtn;
                    seatGrid.add(seatBtn, col, row);
                    seatNum++;
                }
            }

            HBox legend = new HBox(15);
            legend.setAlignment(Pos.CENTER);
            Label availLeg = new Label("Available");
            Label selLeg = new Label("Selected");
            selLeg.setStyle("-fx-background-color: lightgreen; -fx-padding: 2 6;");
            Label resLeg = new Label("Reserved (X)");
            legend.getChildren().addAll(availLeg, selLeg, resLeg);

            Label summaryLabel = new Label("Selected: 0 seats | Total: $0.00");

            Button confirmBtn = new Button("Confirm Reservation");
            confirmBtn.setMaxWidth(Double.MAX_VALUE);
            confirmBtn.setOnAction(e -> {
                List<Integer> selectedSeats = new ArrayList<>();
                for (int r = 0; r < 10; r++) {
                    for (int c = 0; c < 10; c++) {
                        if (seats[r][c].isSelected() && !seats[r][c].isDisabled()) {
                            selectedSeats.add((r * 10) + c + 1);
                        }
                    }
                }
                if (selectedSeats.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Select at least one seat.").showAndWait();
                    return;
                }
                int successCount = 0;
                for (int seat : selectedSeats) {
                    try {
                        if (reservationRepo.reserveSeat(currentUser.getId(), showtime.getId(), seat)) {
                            successCount++;
                        }
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                    }
                }
                if (successCount > 0) {
                    new Alert(Alert.AlertType.INFORMATION, successCount + " seat(s) reserved!").showAndWait();
                    dialog.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Could not reserve seats.").showAndWait();
                }
            });

            root.getChildren().addAll(header, priceLabel, screen, seatGrid, legend, summaryLabel, confirmBtn);
            dialog.setScene(new Scene(root, 500, 600));
            dialog.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
