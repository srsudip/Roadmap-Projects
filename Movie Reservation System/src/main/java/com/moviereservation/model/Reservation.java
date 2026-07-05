package com.moviereservation.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private final int id;
    private final int userId;
    private final int showtimeId;
    private final String movieTitle;
    private final LocalDate showDate;
    private final LocalTime showTime;
    private final int seatNumber;
    private final String status;
    private final double price;

    public Reservation(int id, int userId, int showtimeId, String movieTitle, LocalDate showDate,
                       LocalTime showTime, int seatNumber, String status, double price) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.status = status;
        this.price = price;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getShowtimeId() { return showtimeId; }
    public String getMovieTitle() { return movieTitle; }
    public LocalDate getShowDate() { return showDate; }
    public LocalTime getShowTime() { return showTime; }
    public int getSeatNumber() { return seatNumber; }
    public String getStatus() { return status; }
    public double getPrice() { return price; }
}
