package com.moviereservation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:movie_reservation.db";
    private Connection conn;

    public Database() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
            seedData();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS users ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "username TEXT UNIQUE NOT NULL, "
            + "password_hash TEXT NOT NULL, "
            + "full_name TEXT NOT NULL, "
            + "role TEXT NOT NULL DEFAULT 'USER', "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS movies ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "title TEXT NOT NULL, "
            + "description TEXT, "
            + "genre TEXT NOT NULL, "
            + "duration_minutes INTEGER NOT NULL, "
            + "poster_url TEXT, "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS showtimes ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "movie_id INTEGER NOT NULL, "
            + "show_date DATE NOT NULL, "
            + "show_time TIME NOT NULL, "
            + "total_seats INTEGER NOT NULL DEFAULT 100, "
            + "price REAL NOT NULL DEFAULT 10.0, "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS reservations ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "user_id INTEGER NOT NULL, "
            + "showtime_id INTEGER NOT NULL, "
            + "seat_number INTEGER NOT NULL, "
            + "status TEXT NOT NULL DEFAULT 'ACTIVE', "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY (user_id) REFERENCES users(id), "
            + "FOREIGN KEY (showtime_id) REFERENCES showtimes(id), "
            + "UNIQUE(showtime_id, seat_number)"
            + ")");

        stmt.close();
    }

    private void seedData() throws SQLException {
        Statement check = conn.createStatement();
        ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        if (rs.getInt(1) > 0) {
            rs.close();
            check.close();
            return;
        }
        rs.close();
        check.close();

        PreparedStatement admin = conn.prepareStatement(
            "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)");
        admin.setString(1, "admin");
        admin.setString(2, hashPassword("admin123"));
        admin.setString(3, "System Admin");
        admin.setString(4, "ADMIN");
        admin.executeUpdate();
        admin.close();

        String[][] movies = {
            {"Inception", "A thief who steals corporate secrets through dream-sharing technology.", "Sci-Fi", "148", "https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg"},
            {"The Dark Knight", "Batman faces the Joker, a criminal mastermind who wants to plunge Gotham into anarchy.", "Action", "152", "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911BTUgMe1nM4Rb.jpg"},
            {"Interstellar", "A team of explorers travel through a wormhole in space.", "Sci-Fi", "169", "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"},
            {"The Shawshank Redemption", "Two imprisoned men bond over a number of years.", "Drama", "142", "https://image.tmdb.org/t/p/w500/9cjIGRiQpMpJNU2SoJkwLlUPDcJ.jpg"},
            {"Pulp Fiction", "The lives of two mob hitmen, a boxer, and a pair of diner bandits intertwine.", "Crime", "154", "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg"}
        };

        PreparedStatement movieStmt = conn.prepareStatement(
            "INSERT INTO movies (title, description, genre, duration_minutes, poster_url) VALUES (?, ?, ?, ?, ?)");
        for (String[] m : movies) {
            movieStmt.setString(1, m[0]);
            movieStmt.setString(2, m[1]);
            movieStmt.setString(3, m[2]);
            movieStmt.setInt(4, Integer.parseInt(m[3]));
            movieStmt.setString(5, m[4]);
            movieStmt.executeUpdate();
        }
        movieStmt.close();

        PreparedStatement showtimeStmt = conn.prepareStatement(
            "INSERT INTO showtimes (movie_id, show_date, show_time, total_seats, price) VALUES (?, ?, ?, ?, ?)");

        LocalDate today = LocalDate.now();
        String[] times = {"10:00", "13:30", "17:00", "20:30"};
        double[] prices = {12.0, 12.0, 15.0, 18.0};

        for (int movieId = 1; movieId <= 5; movieId++) {
            for (int dayOffset = 0; dayOffset < 3; dayOffset++) {
                LocalDate showDate = today.plusDays(dayOffset);
                for (int t = 0; t < times.length; t++) {
                    showtimeStmt.setInt(1, movieId);
                    showtimeStmt.setString(2, showDate.toString());
                    showtimeStmt.setString(3, times[t]);
                    showtimeStmt.setInt(4, 100);
                    showtimeStmt.setDouble(5, prices[t]);
                    showtimeStmt.executeUpdate();
                }
            }
        }
        showtimeStmt.close();
    }

    // ======================== AUTH ========================

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User login(String username, String password) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM users WHERE username = ? AND password_hash = ?");
        ps.setString(1, username);
        ps.setString(2, hashPassword(password));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            User user = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("full_name"),
                rs.getString("role")
            );
            rs.close();
            ps.close();
            return user;
        }
        rs.close();
        ps.close();
        return null;
    }

    public boolean signup(String username, String password, String fullName) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, 'USER')");
        ps.setString(1, username);
        ps.setString(2, hashPassword(password));
        ps.setString(3, fullName);
        try {
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            ps.close();
            return false;
        }
    }

    // ======================== MOVIES ========================

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM movies ORDER BY title");
        while (rs.next()) {
            movies.add(new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("duration_minutes"),
                rs.getString("poster_url")
            ));
        }
        rs.close();
        stmt.close();
        return movies;
    }

    public List<Movie> getMoviesByGenre(String genre) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM movies WHERE genre = ? ORDER BY title");
        ps.setString(1, genre);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            movies.add(new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("duration_minutes"),
                rs.getString("poster_url")
            ));
        }
        rs.close();
        ps.close();
        return movies;
    }

    public void addMovie(String title, String description, String genre, int duration, String posterUrl) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO movies (title, description, genre, duration_minutes, poster_url) VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, genre);
        ps.setInt(4, duration);
        ps.setString(5, posterUrl);
        ps.executeUpdate();
        ps.close();
    }

    public void updateMovie(int id, String title, String description, String genre, int duration, String posterUrl) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE movies SET title=?, description=?, genre=?, duration_minutes=?, poster_url=? WHERE id=?");
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, genre);
        ps.setInt(4, duration);
        ps.setString(5, posterUrl);
        ps.setInt(6, id);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteMovie(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM movies WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public void promoteUser(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET role='ADMIN' WHERE id=? AND role='USER'");
        ps.setInt(1, userId);
        ps.executeUpdate();
        ps.close();
    }

    public void demoteUser(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET role='USER' WHERE id=? AND role='ADMIN'");
        ps.setInt(1, userId);
        ps.executeUpdate();
        ps.close();
    }

    public List<Movie> searchMovies(String query) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM movies WHERE title LIKE ? OR genre LIKE ? ORDER BY title");
        String q = "%" + query + "%";
        ps.setString(1, q);
        ps.setString(2, q);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            movies.add(new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("genre"),
                rs.getInt("duration_minutes"),
                rs.getString("poster_url")
            ));
        }
        rs.close();
        ps.close();
        return movies;
    }

    // ======================== SHOWTIMES ========================

    public List<Showtime> getShowtimesForDate(LocalDate date) throws SQLException {
        List<Showtime> showtimes = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT s.*, m.title as movie_title, m.genre, m.duration_minutes "
            + "FROM showtimes s JOIN movies m ON s.movie_id = m.id "
            + "WHERE s.show_date = ? ORDER BY s.show_time");
        ps.setString(1, date.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            showtimes.add(new Showtime(
                rs.getInt("id"),
                rs.getInt("movie_id"),
                rs.getString("movie_title"),
                rs.getString("genre"),
                rs.getString("show_date"),
                rs.getString("show_time"),
                rs.getInt("duration_minutes"),
                rs.getInt("total_seats"),
                rs.getDouble("price")
            ));
        }
        rs.close();
        ps.close();
        return showtimes;
    }

    public List<Showtime> getShowtimesForMovie(int movieId) throws SQLException {
        List<Showtime> showtimes = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT s.*, m.title as movie_title, m.genre, m.duration_minutes "
            + "FROM showtimes s JOIN movies m ON s.movie_id = m.id "
            + "WHERE s.movie_id = ? AND s.show_date >= date('now') "
            + "ORDER BY s.show_date, s.show_time");
        ps.setInt(1, movieId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            showtimes.add(new Showtime(
                rs.getInt("id"),
                rs.getInt("movie_id"),
                rs.getString("movie_title"),
                rs.getString("genre"),
                rs.getString("show_date"),
                rs.getString("show_time"),
                rs.getInt("duration_minutes"),
                rs.getInt("total_seats"),
                rs.getDouble("price")
            ));
        }
        rs.close();
        ps.close();
        return showtimes;
    }

    public void addShowtime(int movieId, LocalDate date, LocalTime time, int totalSeats, double price) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO showtimes (movie_id, show_date, show_time, total_seats, price) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, movieId);
        ps.setString(2, date.toString());
        ps.setString(3, time.toString());
        ps.setInt(4, totalSeats);
        ps.setDouble(5, price);
        ps.executeUpdate();
        ps.close();
    }

    public void deleteShowtime(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM showtimes WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    // ======================== RESERVATIONS ========================

    public int getAvailableSeats(int showtimeId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT total_seats FROM showtimes WHERE id=?");
        ps.setInt(1, showtimeId);
        ResultSet rs = ps.executeQuery();
        int total = rs.next() ? rs.getInt("total_seats") : 0;
        rs.close();
        ps.close();

        PreparedStatement ps2 = conn.prepareStatement(
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
        PreparedStatement ps = conn.prepareStatement(
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
        PreparedStatement check = conn.prepareStatement(
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

        int available = getAvailableSeats(showtimeId);
        if (available <= 0) return false;

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO reservations (user_id, showtime_id, seat_number, status) VALUES (?, ?, ?, 'ACTIVE')");
        ps.setInt(1, userId);
        ps.setInt(2, showtimeId);
        ps.setInt(3, seatNumber);
        ps.executeUpdate();
        ps.close();
        return true;
    }

    public boolean cancelReservation(int reservationId, int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE reservations SET status='CANCELLED' WHERE id=? AND user_id=? AND status='ACTIVE'");
        ps.setInt(1, reservationId);
        ps.setInt(2, userId);
        int updated = ps.executeUpdate();
        ps.close();
        return updated > 0;
    }

    public List<Reservation> getUserReservations(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT r.*, m.title as movie_title, s.show_date, s.show_time, s.price "
            + "FROM reservations r "
            + "JOIN showtimes s ON r.showtime_id = s.id "
            + "JOIN movies m ON s.movie_id = m.id "
            + "WHERE r.user_id = ? AND r.status = 'ACTIVE' "
            + "AND (s.show_date > date('now') OR (s.show_date = date('now') AND s.show_time >= time('now'))) "
            + "ORDER BY s.show_date, s.show_time");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            reservations.add(new Reservation(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("showtime_id"),
                rs.getString("movie_title"),
                rs.getString("show_date"),
                rs.getString("show_time"),
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
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT r.*, m.title as movie_title, u.username, s.show_date, s.show_time, s.price "
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
                rs.getString("show_date"),
                rs.getString("show_time"),
                rs.getInt("seat_number"),
                rs.getString("status"),
                rs.getDouble("price")
            ));
        }
        rs.close();
        stmt.close();
        return reservations;
    }

    // ======================== REPORTS ========================

    public ReportData getReportData() throws SQLException {
        int totalReservations = 0;
        double totalRevenue = 0;
        int totalCapacity = 0;

        Statement stmt = conn.createStatement();

        ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM reservations WHERE status='ACTIVE'");
        if (rs1.next()) totalReservations = rs1.getInt(1);
        rs1.close();

        ResultSet rs2 = stmt.executeQuery(
            "SELECT COALESCE(SUM(s.price), 0) FROM reservations r JOIN showtimes s ON r.showtime_id = s.id WHERE r.status='ACTIVE'");
        if (rs2.next()) totalRevenue = rs2.getDouble(1);
        rs2.close();

        ResultSet rs3 = stmt.executeQuery(
            "SELECT COALESCE(SUM(total_seats), 0) FROM showtimes WHERE show_date >= date('now')");
        if (rs3.next()) totalCapacity = rs3.getInt(1);
        rs3.close();

        stmt.close();
        return new ReportData(totalReservations, totalRevenue, totalCapacity);
    }

    public Connection getConnection() { return conn; }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ======================== DATA CLASSES ========================

    public static class User {
        public final int id;
        public final String username;
        public final String fullName;
        public final String role;

        public User(int id, String username, String fullName, String role) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }

        public boolean isAdmin() { return "ADMIN".equals(role); }
    }

    public static class Movie {
        public final int id;
        public final String title;
        public final String description;
        public final String genre;
        public final int durationMinutes;
        public final String posterUrl;

        public Movie(int id, String title, String description, String genre, int durationMinutes, String posterUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.genre = genre;
            this.durationMinutes = durationMinutes;
            this.posterUrl = posterUrl;
        }

        @Override
        public String toString() { return title + " (" + genre + ") - " + durationMinutes + " min"; }
    }

    public static class Showtime {
        public final int id;
        public final int movieId;
        public final String movieTitle;
        public final String genre;
        public final String showDate;
        public final String showTime;
        public final int durationMinutes;
        public final int totalSeats;
        public final double price;

        public Showtime(int id, int movieId, String movieTitle, String genre, String showDate, String showTime,
                        int durationMinutes, int totalSeats, double price) {
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

        @Override
        public String toString() { return movieTitle + " - " + showDate + " " + showTime + " ($" + price + ")"; }
    }

    public static class Reservation {
        public final int id;
        public final int userId;
        public final int showtimeId;
        public final String movieTitle;
        public final String showDate;
        public final String showTime;
        public final int seatNumber;
        public final String status;
        public final double price;

        public Reservation(int id, int userId, int showtimeId, String movieTitle, String showDate,
                          String showTime, int seatNumber, String status, double price) {
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
    }

    public static class ReportData {
        public final int totalReservations;
        public final double totalRevenue;
        public final int totalCapacity;

        public ReportData(int totalReservations, double totalRevenue, int totalCapacity) {
            this.totalReservations = totalReservations;
            this.totalRevenue = totalRevenue;
            this.totalCapacity = totalCapacity;
        }
    }
}
