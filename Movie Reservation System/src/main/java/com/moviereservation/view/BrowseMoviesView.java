package com.moviereservation.view;

import com.moviereservation.model.Showtime;
import com.moviereservation.model.User;
import com.moviereservation.repository.MovieRepository;
import com.moviereservation.repository.ReservationRepository;
import com.moviereservation.repository.ShowtimeRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

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
        genreFilter.getItems().addAll("All", "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Crime", "Thriller");
        genreFilter.setValue("All");
        Button refreshBtn = new Button("Show Times");
        controls.getChildren().addAll(new Label("Date:"), datePicker, new Label("Genre:"), genreFilter, refreshBtn);

        VBox movieList = new VBox(8);
        ScrollPane scrollPane = new ScrollPane(movieList);
        scrollPane.setFitToWidth(true);

        Runnable loadMovies = () -> {
            movieList.getChildren().clear();
            try {
                List<Showtime> showtimes = showtimeRepo.getShowtimesForDate(datePicker.getValue());
                String genre = genreFilter.getValue();
                if (showtimes.isEmpty()) {
                    movieList.getChildren().add(new Label("No showtimes for this date."));
                    return;
                }
                for (Showtime st : showtimes) {
                    if (!"All".equals(genre) && !st.getGenre().equals(genre)) continue;
                    int available = reservationRepo.getAvailableSeats(st.getId());
                    movieList.getChildren().add(createShowtimeCard(st, available));
                }
            } catch (Exception e) {
                movieList.getChildren().add(new Label("Error: " + e.getMessage()));
            }
        };

        refreshBtn.setOnAction(e -> loadMovies.run());
        genreFilter.setOnAction(e -> loadMovies.run());
        loadMovies.run();

        content.getChildren().addAll(header, controls, scrollPane);
        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }

    private HBox createShowtimeCard(Showtime st, int available) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 4;");
        card.setAlignment(Pos.CENTER_LEFT);

        Label info = new Label(st.getMovieTitle() + " | " + st.getGenre() + " | "
            + st.getDurationMinutes() + " min | " + st.getShowTime() + " | "
            + available + " seats | $" + String.format("%.2f", st.getPrice()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button reserveBtn = new Button("Reserve");
        reserveBtn.setDisable(available <= 0);
        reserveBtn.setOnAction(e -> showSeatSelection(st));

        card.getChildren().addAll(info, spacer, reserveBtn);
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
                    int currentSeat = seatNum;
                    ToggleButton seatBtn = new ToggleButton(String.valueOf(seatNum));
                    seatBtn.setPrefSize(38, 38);

                    if (reservedSeats.contains(seatNum)) {
                        seatBtn.setDisable(true);
                        seatBtn.setSelected(true);
                        seatBtn.setText("X");
                    }

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
                List<Integer> selectedSeats = new java.util.ArrayList<>();
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
