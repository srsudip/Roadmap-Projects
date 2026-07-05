# Movie Reservation System

A full-featured movie reservation system with a JavaFX GUI, built as part of the [roadmap.sh](https://roadmap.sh/projects/movie-reservation-system) backend projects.

## Features

### User Features
- Sign up / Login with role-based access
- Browse movies by date and genre
- Interactive 10x10 seat grid with real-time availability
- Reserve and cancel upcoming reservations

### Admin Features
- Add, update, and delete movies
- Add and delete showtimes
- Promote/demote users
- Reports: total reservations, revenue, capacity utilization

## Architecture

```
src/main/java/com/moviereservation/
├── App.java                          # Entry point
├── db/
│   └── DatabaseManager.java          # H2 connection, schema, seeding
├── model/
│   ├── User.java
│   ├── Movie.java
│   ├── Showtime.java
│   └── Reservation.java
├── repository/
│   ├── UserRepository.java
│   ├── MovieRepository.java
│   ├── ShowtimeRepository.java
│   └── ReservationRepository.java
└── view/
    ├── LoginView.java
    ├── SignupView.java
    ├── MainLayout.java
    ├── BrowseMoviesView.java
    ├── MyReservationsView.java
    ├── ManageMoviesView.java
    ├── ManageShowtimesView.java
    ├── ReportsView.java
    └── UsersView.java
```

## Tech Stack

- **Language:** Java 21
- **GUI:** JavaFX 21.0.2
- **Database:** H2 (embedded)
- **Build Tool:** Maven

## How to Run

```bash
cd "Movie Reservation System"
mvn compile
mvn javafx:run
```

## Default Credentials

| Username | Password   | Role  |
|----------|------------|-------|
| admin    | admin123   | ADMIN |

## Data Model

- **users**: id, username, password_hash, full_name, role (USER/ADMIN)
- **movies**: id, title, description, genre, duration_minutes, poster_url
- **showtimes**: id, movie_id, show_date, show_time, total_seats, price
- **reservations**: id, user_id, showtime_id, seat_number, status (ACTIVE/CANCELLED)

## Seed Data

Pre-loaded with 5 movies, 60 showtimes (4/day x 3 days x 5 movies), and 1 admin user.
