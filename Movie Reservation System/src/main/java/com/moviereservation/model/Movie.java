package com.moviereservation.model;

public class Movie {
    private final int id;
    private final String title;
    private final String description;
    private final String genre;
    private final int durationMinutes;
    private final String posterUrl;

    public Movie(int id, String title, String description, String genre, int durationMinutes, String posterUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.posterUrl = posterUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getPosterUrl() { return posterUrl; }

    @Override
    public String toString() { return title + " (" + genre + ")"; }
}
