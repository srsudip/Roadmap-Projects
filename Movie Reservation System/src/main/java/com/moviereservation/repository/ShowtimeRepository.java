package com.moviereservation.repository;

import com.moviereservation.db.DatabaseManager;
import com.moviereservation.model.Showtime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Showtime> getShowtimesForDate(LocalDate date) throws SQLException {
        List<Showtime> showtimes = new ArrayList<>();
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT s.*, m.title AS movie_title, m.genre, m.duration_minutes "
            + "FROM showtimes s JOIN movies m ON s.movie_id = m.id "
            + "WHERE s.show_date = ? ORDER BY s.show_time");
        ps.setDate(1, java.sql.Date.valueOf(date));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            showtimes.add(mapShowtime(rs));
        }
        rs.close();
        ps.close();
        return showtimes;
    }

    public List<Showtime> getShowtimesForMovie(int movieId) throws SQLException {
        List<Showtime> showtimes = new ArrayList<>();
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT s.*, m.title AS movie_title, m.genre, m.duration_minutes "
            + "FROM showtimes s JOIN movies m ON s.movie_id = m.id "
            + "WHERE s.movie_id = ? AND s.show_date >= CURRENT_DATE "
            + "ORDER BY s.show_date, s.show_time");
        ps.setInt(1, movieId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            showtimes.add(mapShowtime(rs));
        }
        rs.close();
        ps.close();
        return showtimes;
    }

    public void addShowtime(int movieId, LocalDate date, LocalTime time, int totalSeats, double price) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "INSERT INTO showtimes (movie_id, show_date, show_time, total_seats, price) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, movieId);
        ps.setDate(2, java.sql.Date.valueOf(date));
        ps.setTime(3, java.sql.Time.valueOf(time));
        ps.setInt(4, totalSeats);
        ps.setDouble(5, price);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteShowtime(int id) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM showtimes WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    private Showtime mapShowtime(ResultSet rs) throws SQLException {
        return new Showtime(
            rs.getInt("id"),
            rs.getInt("movie_id"),
            rs.getString("movie_title"),
            rs.getString("genre"),
            rs.getDate("show_date").toLocalDate(),
            rs.getTime("show_time").toLocalTime(),
            rs.getInt("duration_minutes"),
            rs.getInt("total_seats"),
            rs.getDouble("price")
        );
    }
}
