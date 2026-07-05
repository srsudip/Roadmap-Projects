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
            {"Interstellar", "A team of explorers travel through a wormhole in space.", "Sci-Fi", "169"},
            {"The Matrix", "A hacker discovers reality is a simulation.", "Sci-Fi", "136"},
            {"Blade Runner 2049", "A new blade runner unearths a secret that could plunge what's left of society into chaos.", "Sci-Fi", "164"},
            {"Arrival", "A linguist works with the military to communicate with alien lifeforms.", "Sci-Fi", "116"},
            {"Dune", "Paul Atreides must travel to the most dangerous planet in the universe.", "Sci-Fi", "155"},
            {"Ex Machina", "A programmer is selected to participate in a groundbreaking experiment in synthetic intelligence.", "Sci-Fi", "108"},
            {"Gravity", "Two astronauts struggle to survive after debris destroys their space shuttle.", "Sci-Fi", "91"},
            {"The Martian", "An astronaut becomes stranded on Mars and must use his ingenuity to survive.", "Sci-Fi", "144"},
            {"Tenet", "A secret agent journeys through a world of international espionage.", "Sci-Fi", "150"},

            {"The Dark Knight", "Batman faces the Joker, a criminal mastermind.", "Action", "152"},
            {"Mad Max: Fury Road", "In a post-apocalyptic wasteland, a woman rebels against a tyrannical ruler.", "Action", "120"},
            {"John Wick", "An ex-hitman comes out of retirement to track down the gangsters that killed his dog.", "Action", "101"},
            {"Gladiator", "A former Roman General sets out to exact vengeance against the emperor who murdered his family.", "Action", "155"},
            {"Die Hard", "An NYPD officer tries to save his wife and several others taken hostage by German terrorists.", "Action", "132"},
            {"The Avengers", "Earth's mightiest heroes must come together to stop an alien invasion.", "Action", "143"},
            {"Kill Bill: Vol. 1", "A woman seeks revenge against her former team of assassins.", "Action", "111"},
            {"Top Gun: Maverick", "A naval aviator returns after thirty years to train a new generation.", "Action", "130"},
            {"Casino Royale", "James Bond earns his license to kill in a high-stakes poker game.", "Action", "144"},
            {"The Raid", "A SWAT team becomes trapped in a tenement run by a ruthless mobster.", "Action", "101"},

            {"The Shawshank Redemption", "Two imprisoned men bond over a number of years.", "Drama", "142"},
            {"Forrest Gump", "The story of a man with a low IQ who achieved great things.", "Drama", "142"},
            {"Schindler's List", "In German-occupied Poland, Oskar Schindler becomes concerned for his Jewish workforce.", "Drama", "195"},
            {"The Godfather", "The aging patriarch of an organized crime dynasty transfers control to his reluctant son.", "Drama", "175"},
            {"Good Will Hunting", "Will Hunting, a janitor at MIT, has a gift for mathematics.", "Drama", "126"},
            {"Fight Club", "An insomniac office worker forms an underground fight club.", "Drama", "139"},
            {"The Green Mile", "A death row corrections officer experiences supernatural events.", "Drama", "189"},
            {"12 Years a Slave", "A free-born Black man from New York is kidnapped and sold into slavery.", "Drama", "134"},
            {"Whiplash", "A promising young drummer enrolls at a cut-throat music conservatory.", "Drama", "106"},
            {"Parasite", "Greed and class discrimination threaten the relationship between a wealthy family.", "Drama", "132"},

            {"Pulp Fiction", "The lives of two mob hitmen intertwine with other tales of violence.", "Crime", "154"},
            {"Superbad", "Two co-dependent high school seniors are forced to deal with separation anxiety.", "Comedy", "113"},
            {"The Grand Budapest Hotel", "A writer encounters the owner of an aging high-class hotel.", "Comedy", "99"},
            {"Bridesmaids", "Competition between the maid of honor and a bridesmaid threatens to upend the wedding.", "Comedy", "125"},
            {"Step Brothers", "Two aimless middle-aged losers still living at home become unlikely stepbrothers.", "Comedy", "98"},

            {"Get Out", "A young African-American visits his white girlfriend's parents for the weekend.", "Horror", "104"},
            {"The Conjuring", "Paranormal investigators work to help a family terrorized by a dark presence.", "Horror", "112"},
            {"A Quiet Place", "A family must live in silence to avoid creatures that hunt by sound.", "Horror", "90"},
            {"Hereditary", "A grieving family is haunted by tragic and disturbing occurrences.", "Horror", "127"},
            {"The Shining", "A family heads to an isolated hotel for the winter where an evil presence drives the father insane.", "Horror", "146"},

            {"Gone Girl", "With his wife's disappearance having become the focus of an intense media circus.", "Thriller", "149"},
            {"Se7en", "Two detectives hunt a serial killer who uses the seven deadly sins as motives.", "Thriller", "127"},
            {"The Silence of the Lambs", "A young FBI cadet must receive the help of an incarcerated cannibal killer.", "Thriller", "118"},
            {"Shutter Island", "A U.S. Marshal investigates the disappearance of a murderer who escaped from a hospital.", "Thriller", "138"},
            {"Prisoners", "When his daughter and her friend go missing, a father takes matters into his own hands.", "Thriller", "153"},

            {"Spirited Away", "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods.", "Animation", "125"},
            {"Coco", "Aspiring musician Miguel enters the Land of the Dead to find his great-great-grandfather.", "Animation", "105"},
            {"Spider-Man: Into the Spider-Verse", "Teen Miles Morales becomes the Spider-Man of his reality.", "Animation", "117"},
            {"The Lion King", "A young lion prince flees his kingdom only to learn the true meaning of responsibility.", "Animation", "88"},
            {"WALL-E", "In the distant future, a small waste-collecting robot embarks on a space journey.", "Animation", "98"}
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

        int totalMovies = movies.length;
        for (int movieId = 1; movieId <= totalMovies; movieId++) {
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
