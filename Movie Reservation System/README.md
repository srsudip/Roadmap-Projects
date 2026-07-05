# Movie Reservation System

A full-featured movie reservation system with a JavaFX GUI, built as part of the [roadmap.sh](https://roadmap.sh/projects/movie-reservation-system) backend projects.

## Features

### User Features
- **Sign Up / Login** — Create an account or sign in with existing credentials
- **Browse Movies** — View all available movies with date picker and genre filter
- **Seat Selection** — Interactive 10x10 seat grid with real-time availability
- **Reserve Seats** — Book seats for any available showtime
- **View Reservations** — See all your upcoming reservations
- **Cancel Reservations** — Cancel bookings for upcoming shows

### Admin Features
- **Manage Movies** — Add, update, and delete movies from the system
- **Manage Showtimes** — Add and delete showtimes for any movie
- **View Reports** — See total reservations, revenue, capacity utilization, and recent bookings
- **User Management** — View all registered users and their roles

## Tech Stack

- **Language:** Java 21
- **GUI:** JavaFX 21.0.2
- **Database:** SQLite (via JDBC)
- **Build Tool:** Maven

## Prerequisites

- Java 21 or later
- Maven 3.6+

## How to Run

```bash
# Navigate to the project directory
cd "Movie Reservation System"

# Compile the project
mvn compile

# Run the application
mvn javafx:run
```

## Default Credentials

| Username | Password   | Role  |
|----------|------------|-------|
| admin    | admin123   | ADMIN |

You can also create a new account via the Sign Up page.

## Data Model

```
users ──────────┐
                 │
showtimes ───────┤
    │            │
    └── reservations
```

- **users**: id, username, password_hash, full_name, role (USER/ADMIN)
- **movies**: id, title, description, genre, duration_minutes, poster_url
- **showtimes**: id, movie_id, show_date, show_time, total_seats, price
- **reservations**: id, user_id, showtime_id, seat_number, status (ACTIVE/CANCELLED)

## Sample Seed Data

The application comes pre-loaded with:
- **5 Movies**: Inception, The Dark Knight, Interstellar, The Shawshank Redemption, Pulp Fiction
- **20 Showtimes**: 4 per movie per day across 3 days (morning, afternoon, evening, night)
- **1 Admin user**: admin / admin123
