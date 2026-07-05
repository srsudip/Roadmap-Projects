# Roadmap Projects

A collection of backend development projects completed as part of the **[roadmap.sh](https://roadmap.sh)** learning path. Each project focuses on building practical CLI tools and applications to strengthen core programming skills.

**Main Roadmap Path:** [https://roadmap.sh](https://roadmap.sh)

---

## Projects

| # | Project | Description | Link |
|---|---------|-------------|------|
| 1 | **Task Tracker CLI** | A CLI app to manage tasks and to-do lists with JSON persistence | [roadmap.sh/projects/task-tracker](https://roadmap.sh/projects/task-tracker) |
| 2 | **GitHub User Activity** | A CLI app to fetch and display recent GitHub activity for any user | [roadmap.sh/projects/github-user-activity](https://roadmap.sh/projects/github-user-activity) |
| 3 | **Expense Tracker** | A CLI + JavaFX GUI app to manage finances — add, delete, view expenses and summaries | [roadmap.sh/projects/expense-tracker](https://roadmap.sh/projects/expense-tracker) |
| 4 | **Number Guessing Game** | A CLI game with difficulty levels, hints, timer, and high score tracking | [roadmap.sh/projects/number-guessing-game](https://roadmap.sh/projects/number-guessing-game) |
| 5 | **Movie Reservation System** | A JavaFX GUI app with auth, seat reservation, showtimes, and admin reporting | [roadmap.sh/projects/movie-reservation-system](https://roadmap.sh/projects/movie-reservation-system) |
| 6 | **Scalable E-Commerce Platform** | A microservices-based e-commerce platform with Docker, Spring Cloud, Thymeleaf frontend, and RabbitMQ | [roadmap.sh/projects/scalable-ecommerce-platform](https://roadmap.sh/projects/scalable-ecommerce-platform) |

---

## Technologies Used

- **Language:** Java 21
- **GUI:** JavaFX
- **Backend:** Spring Boot, Spring Cloud, Microservices
- **Database:** SQLite, PostgreSQL, H2
- **Messaging:** RabbitMQ
- **Infrastructure:** Docker, Docker Compose, GitHub Actions CI/CD
- **Approach:** CLI tools, GUI apps, and microservices architectures
- **Focus:** CLI tools, API integration, data persistence, microservices, and clean code architecture

---

## E-Commerce Platform — Production Ready

The E-Commerce Platform (project #6) has been built with production-grade patterns:

- **Multi-stage Docker builds** — `development` (hot-reload) and `production` (minimal, non-root)
- **Dev/Prod separation** — `docker-compose.yml` for development, `docker-compose-prod.yml` for production
- **CI/CD** — GitHub Actions pipeline: test → build → push to GHCR → deploy
- **Database isolation** — 6 separate PostgreSQL instances with init scripts
- **Event-driven notifications** — order service publishes order events to RabbitMQ; notification service consumes and persists them
- **16 containers** — 8 backend microservices + 6 databases + RabbitMQ + Frontend
- **One-command run** — `python3 run.py` builds, starts, waits for health, and cleans up on Ctrl+C

See [`E-Commerce Platform/RUNNER.md`](E-Commerce%20Platform/RUNNER.md) for step-by-step setup instructions.

---

## About

This repository documents my journey through the backend developer roadmap on [roadmap.sh](https://roadmap.sh). Each project is designed to build hands-on experience with fundamental backend concepts.
