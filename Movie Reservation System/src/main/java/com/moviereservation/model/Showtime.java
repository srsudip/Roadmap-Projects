package com.moviereservation.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Showtime {
    private final int id;
    private final int movieId;
    private final String movieTitle;
    private final String genre;
    private final LocalDate showDate;
    private final LocalTime showTime;
    private final int durationMinutes;
    private final int totalSeats;
    private final double price;

    public Showtime(int id, int movieId, String movieTitle, String genre, LocalDate showDate,
                    LocalTime showTime, int durationMinutes, int totalSeats, double price) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.genre = genre;
        this.showDate = showDate;
        this.showTime = showTime;
        this.durationMinutes = durationMinutes;
        this.totalSeats = totalSeats;
        this.price = price;
    }

    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public String getGenre() { return genre; }
    public LocalDate getShowDate() { return showDate; }
    public LocalTime getShowTime() { return showTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getTotalSeats() { return totalSeats; }
    public double getPrice() { return price; }

    @Override
    public String toString() { return movieTitle + " - " + showDate + " " + showTime; }
}
