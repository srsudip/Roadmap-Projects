package com.moviereservation.repository;

import com.moviereservation.db.DatabaseManager;
import com.moviereservation.model.Reservation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public int getAvailableSeats(int showtimeId) throws SQLException {
        PreparedStatement ps1 = db.getConnection().prepareStatement(
            "SELECT total_seats FROM showtimes WHERE id=?");
        ps1.setInt(1, showtimeId);
        ResultSet rs1 = ps1.executeQuery();
        int total = rs1.next() ? rs1.getInt("total_seats") : 0;
        rs1.close();
        ps1.close();

        PreparedStatement ps2 = db.getConnection().prepareStatement(
            "SELECT COUNT(*) FROM reservations WHERE showtime_id=? AND status='ACTIVE'");
        ps2.setInt(1, showtimeId);
        ResultSet rs2 = ps2.executeQuery();
        int reserved = rs2.next() ? rs2.getInt(1) : 0;
        rs2.close();
        ps2.close();

        return total - reserved;
    }

    public List<Integer> getReservedSeats(int showtimeId) throws SQLException {
        List<Integer> seats = new ArrayList<>();
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT seat_number FROM reservations WHERE showtime_id=? AND status='ACTIVE'");
        ps.setInt(1, showtimeId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            seats.add(rs.getInt("seat_number"));
        }
        rs.close();
        ps.close();
        return seats;
    }

    public boolean reserveSeat(int userId, int showtimeId, int seatNumber) throws SQLException {
        PreparedStatement check = db.getConnection().prepareStatement(
            "SELECT COUNT(*) FROM reservations WHERE showtime_id=? AND seat_number=? AND status='ACTIVE'");
        check.setInt(1, showtimeId);
        check.setInt(2, seatNumber);
        ResultSet rs = check.executeQuery();
        rs.next();
        if (rs.getInt(1) > 0) {
            rs.close();
            check.close();
            return false;
        }
        rs.close();
        check.close();

        if (getAvailableSeats(showtimeId) <= 0) return false;

        PreparedStatement ps = db.getConnection().prepareStatement(
            "INSERT INTO reservations (user_id, showtime_id, seat_number, status) VALUES (?, ?, ?, 'ACTIVE')");
        ps.setInt(1, userId);
        ps.setInt(2, showtimeId);
        ps.setInt(3, seatNumber);
        ps.executeUpdate();
        ps.close();
        return true;
    }

    public boolean cancelReservation(int reservationId, int userId) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
            "UPDATE reservations SET status='CANCELLED' WHERE id=? AND user_id=? AND status='ACTIVE'");
        ps.setInt(1, reservationId);
        ps.setInt(2, userId);
        int updated = ps.executeUpdate();
        ps.close();
        return updated > 0;
    }

    public List<Reservation> getUserReservations(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT r.*, m.title AS movie_title, s.show_date, s.show_time, s.price "
            + "FROM reservations r "
            + "JOIN showtimes s ON r.showtime_id = s.id "
            + "JOIN movies m ON s.movie_id = m.id "
            +             "WHERE r.user_id = ? AND r.status = 'ACTIVE' "
            + "AND s.show_date >= CURRENT_DATE "
            + "ORDER BY s.show_date, s.show_time");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            reservations.add(new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("showtime_id"),
                rs.getString("movie_title"),
                rs.getDate("show_date").toLocalDate(),
                rs.getTime("show_time").toLocalTime(),
                rs.getInt("seat_number"),
                rs.getString("status"),
                rs.getDouble("price")
            ));
        }
        rs.close();
        ps.close();
        return reservations;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        var rs = db.getConnection().createStatement().executeQuery(
            "SELECT r.*, m.title AS movie_title, u.username, s.show_date, s.show_time, s.price "
            + "FROM reservations r "
            + "JOIN showtimes s ON r.showtime_id = s.id "
            + "JOIN movies m ON s.movie_id = m.id "
            + "JOIN users u ON r.user_id = u.id "
            + "WHERE r.status = 'ACTIVE' "
            + "ORDER BY s.show_date, s.show_time");
        while (rs.next()) {
            reservations.add(new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("showtime_id"),
                rs.getString("movie_title") + " (" + rs.getString("username") + ")",
                rs.getDate("show_date").toLocalDate(),
                rs.getTime("show_time").toLocalTime(),
                rs.getInt("seat_number"),
                rs.getString("status"),
                rs.getDouble("price")
            ));
        }
        rs.close();
        return reservations;
    }

    public int getTotalReservations() throws SQLException {
        var rs = db.getConnection().createStatement().executeQuery(
            "SELECT COUNT(*) FROM reservations WHERE status='ACTIVE'");
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        return count;
    }

    public double getTotalRevenue() throws SQLException {
        var rs = db.getConnection().createStatement().executeQuery(
            "SELECT COALESCE(SUM(s.price), 0) FROM reservations r JOIN showtimes s ON r.showtime_id = s.id WHERE r.status='ACTIVE'");
        rs.next();
        double revenue = rs.getDouble(1);
        rs.close();
        return revenue;
    }

    public int getTotalCapacity() throws SQLException {
        var rs = db.getConnection().createStatement().executeQuery(
            "SELECT COALESCE(SUM(total_seats), 0) FROM showtimes WHERE show_date >= CURRENT_DATE");
        rs.next();
        int capacity = rs.getInt(1);
        rs.close();
        return capacity;
    }
}
