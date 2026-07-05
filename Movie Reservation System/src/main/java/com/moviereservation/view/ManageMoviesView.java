package com.moviereservation.view;

import com.moviereservation.model.Movie;
import com.moviereservation.repository.MovieRepository;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ManageMoviesView {
    private final Runnable refreshCallback;
    private final MovieRepository movieRepo = new MovieRepository();

    public ManageMoviesView(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public ScrollPane createView() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label header = new Label("Manage Movies");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(6);
        form.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10;");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(2);
        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.getItems().addAll("Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Crime", "Thriller");
        genreBox.setValue("Action");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (min)");
        TextField posterField = new TextField();
        posterField.setPromptText("Poster URL");
        Button addBtn = new Button("Add Movie");

        form.add(new Label("Title:"), 0, 0); form.add(titleField, 1, 0);
        form.add(new Label("Genre:"), 0, 1); form.add(genreBox, 1, 1);
        form.add(new Label("Duration:"), 0, 2); form.add(durationField, 1, 2);
        form.add(new Label("Description:"), 0, 3); form.add(descArea, 1, 3);
        form.add(new Label("Poster URL:"), 0, 4); form.add(posterField, 1, 4);
        form.add(addBtn, 1, 5);

        addBtn.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Title is required.").showAndWait();
                    return;
                }
                movieRepo.addMovie(title, descArea.getText().trim(), genreBox.getValue(),
                    Integer.parseInt(durationField.getText().trim()), posterField.getText().trim());
                new Alert(Alert.AlertType.INFORMATION, "Movie added!").showAndWait();
                titleField.clear(); descArea.clear(); durationField.clear(); posterField.clear();
                refreshCallback.run();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Duration must be a number.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        VBox movieList = new VBox(6);
        try {
            List<Movie> movies = movieRepo.getAllMovies();
            for (Movie m : movies) {
                HBox card = new HBox(10);
                card.setPadding(new Insets(6));
                card.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

                Label info = new Label(m.getTitle() + " | " + m.getGenre() + " | " + m.getDurationMinutes() + " min");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button editBtn = new Button("Edit");
                editBtn.setOnAction(e -> showEditDialog(m));

                Button deleteBtn = new Button("Delete");
                deleteBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this movie?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                movieRepo.deleteMovie(m.getId());
                                refreshCallback.run();
                            } catch (Exception ex) {
                                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                            }
                        }
                    });
                });

                card.getChildren().addAll(info, spacer, editBtn, deleteBtn);
                movieList.getChildren().add(card);
            }
        } catch (Exception e) {
            movieList.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        ScrollPane scrollPane = new ScrollPane(movieList);
        scrollPane.setFitToWidth(true);
        content.getChildren().addAll(header, form, new Separator(), scrollPane);
        ScrollPane outer = new ScrollPane(content);
        outer.setFitToWidth(true);
        return outer;
    }

    private void showEditDialog(Movie movie) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Movie");

        VBox vbox = new VBox(8);
        vbox.setPadding(new Insets(15));

        TextField titleField = new TextField(movie.getTitle());
        TextArea descArea = new TextArea(movie.getDescription() != null ? movie.getDescription() : "");
        descArea.setPrefRowCount(2);
        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.getItems().addAll("Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance", "Crime", "Thriller");
        genreBox.setValue(movie.getGenre());
        TextField durationField = new TextField(String.valueOf(movie.getDurationMinutes()));
        TextField posterField = new TextField(movie.getPosterUrl() != null ? movie.getPosterUrl() : "");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(6);
        form.add(new Label("Title:"), 0, 0); form.add(titleField, 1, 0);
        form.add(new Label("Genre:"), 0, 1); form.add(genreBox, 1, 1);
        form.add(new Label("Duration:"), 0, 2); form.add(durationField, 1, 2);
        form.add(new Label("Description:"), 0, 3); form.add(descArea, 1, 3);
        form.add(new Label("Poster URL:"), 0, 4); form.add(posterField, 1, 4);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                movieRepo.updateMovie(movie.getId(), titleField.getText().trim(),
                    descArea.getText().trim(), genreBox.getValue(),
                    Integer.parseInt(durationField.getText().trim()), posterField.getText().trim());
                dialog.close();
                refreshCallback.run();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Duration must be a number.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        vbox.getChildren().addAll(form, buttons);
        dialog.setScene(new Scene(vbox, 420, 320));
        dialog.show();
    }
}
