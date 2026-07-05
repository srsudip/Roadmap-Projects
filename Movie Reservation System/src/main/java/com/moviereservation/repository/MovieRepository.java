package com.moviereservation.repository;

import com.moviereservation.db.DatabaseManager;
import com.moviereservation.model.Movie;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        var rs = db.getConnection().createStatement().executeQuery("SELECT * FROM movies ORDER BY title");
        while (rs.next()) {
            movies.add(mapMovie(rs));
        }
        rs.close();
        return movies;
    }

    public List<Movie> getMoviesByGenre(String genre) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        PreparedStatement ps = db.getConnection().prepareStatement(
            "SELECT * FROM movies WHERE genre = ? ORDER BY title");
        ps.setString(1, genre);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            movies.add(mapMovie(rs));
        }
        rs.close();
        ps.close();
        return movies;
    }

    public Movie getMovieById(int id) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement("SELECT * FROM movies WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Movie m = mapMovie(rs);
            rs.close();
            ps.close();
            return m;
        }
        rs.close();
        ps.close();
        return null;
    }

    public void addMovie(String title, String description, String genre, int duration, String posterUrl) throws SQLException {
        PreparedStatement ps = db.getConnection().prepareStatement(
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
        PreparedStatement ps = db.getConnection().prepareStatement(
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
        PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM movies WHERE id=?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    private Movie mapMovie(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("genre"),
            rs.getInt("duration_minutes"),
            rs.getString("poster_url")
        );
    }
}
