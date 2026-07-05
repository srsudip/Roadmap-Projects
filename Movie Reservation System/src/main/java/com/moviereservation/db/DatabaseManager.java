package com.moviereservation.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:~/movie_reservation";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTables();
            seedData();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS users ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "username VARCHAR(50) UNIQUE NOT NULL, "
            + "password_hash VARCHAR(64) NOT NULL, "
            + "full_name VARCHAR(100) NOT NULL, "
            + "role VARCHAR(10) NOT NULL DEFAULT 'USER', "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS movies ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "title VARCHAR(200) NOT NULL, "
            + "description TEXT, "
            + "genre VARCHAR(50) NOT NULL, "
            + "duration_minutes INT NOT NULL, "
            + "poster_url VARCHAR(500), "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS showtimes ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "movie_id INT NOT NULL, "
            + "show_date DATE NOT NULL, "
            + "show_time TIME NOT NULL, "
            + "total_seats INT NOT NULL DEFAULT 100, "
            + "price DOUBLE NOT NULL DEFAULT 10.0, "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE"
            + ")");

        stmt.execute("CREATE TABLE IF NOT EXISTS reservations ("
            + "id INT AUTO_INCREMENT PRIMARY KEY, "
            + "user_id INT NOT NULL, "
            + "showtime_id INT NOT NULL, "
            + "seat_number INT NOT NULL, "
            + "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', "
            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY (user_id) REFERENCES users(id), "
            + "FOREIGN KEY (showtime_id) REFERENCES showtimes(id), "
            + "UNIQUE(showtime_id, seat_number)"
            + ")");

        stmt.close();
    }

    private void seedData() throws SQLException {
        Statement check = connection.createStatement();
        var rs = check.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        if (rs.getInt(1) > 0) {
            rs.close();
            check.close();
            return;
        }
        rs.close();
        check.close();

        var admin = connection.prepareStatement(
            "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)");
        admin.setString(1, "admin");
        admin.setString(2, hashPassword("admin123"));
        admin.setString(3, "System Admin");
        admin.setString(4, "ADMIN");
        admin.executeUpdate();
        admin.close();

        String[][] movies = {
            {"Inception", "A thief who steals corporate secrets through dream-sharing technology.", "Sci-Fi", "148"},
            {"The Dark Knight", "Batman faces the Joker, a criminal mastermind.", "Action", "152"},
            {"Interstellar", "A team of explorers travel through a wormhole in space.", "Sci-Fi", "169"},
            {"The Shawshank Redemption", "Two imprisoned men bond over a number of years.", "Drama", "142"},
            {"Pulp Fiction", "The lives of two mob hitmen intertwine with other tales of violence.", "Crime", "154"}
        };

        var movieStmt = connection.prepareStatement(
            "INSERT INTO movies (title, description, genre, duration_minutes) VALUES (?, ?, ?, ?)");
        for (String[] m : movies) {
            movieStmt.setString(1, m[0]);
            movieStmt.setString(2, m[1]);
            movieStmt.setString(3, m[2]);
            movieStmt.setInt(4, Integer.parseInt(m[3]));
            movieStmt.executeUpdate();
        }
        movieStmt.close();

        var showtimeStmt = connection.prepareStatement(
            "INSERT INTO showtimes (movie_id, show_date, show_time, total_seats, price) VALUES (?, ?, ?, ?, ?)");
        String[] times = {"10:00", "13:30", "17:00", "20:30"};
        double[] prices = {12.0, 12.0, 15.0, 18.0};

        for (int movieId = 1; movieId <= 5; movieId++) {
            for (int dayOffset = 0; dayOffset < 3; dayOffset++) {
                var showDate = java.time.LocalDate.now().plusDays(dayOffset);
                for (int t = 0; t < times.length; t++) {
                    showtimeStmt.setInt(1, movieId);
                    showtimeStmt.setDate(2, java.sql.Date.valueOf(showDate));
                    showtimeStmt.setTime(3, java.sql.Time.valueOf(times[t]));
                    showtimeStmt.setInt(4, 100);
                    showtimeStmt.setDouble(5, prices[t]);
                    showtimeStmt.executeUpdate();
                }
            }
        }
        showtimeStmt.close();
    }

    public static String hashPassword(String password) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            var sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
