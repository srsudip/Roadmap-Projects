# RUNNER.md — E-Commerce Platform

## Prerequisites

| Tool | Version |
|------|---------|
| **Docker Desktop** | 24+ |
| **JDK** | 21 |
| **Maven** | 3.9+ |

Jars are built on the host (in-container Maven downloads get rate-limited by Maven Central).

## Build & Start

**Option A — one command (recommended):**

```bash
python3 run.py
```

Starts from a clean slate every time: removes any previous session's containers/images/volumes, runs `mvn clean package`, builds images, starts everything, waits for health, prints URLs, tails logs. Ctrl+C stops and cleans up all containers, images, volumes, and Maven build artifacts.

**Option B — manual:**

```bash
(cd backend && mvn clean package -DskipTests)
(cd frontend/frontend-service && mvn clean package -DskipTests)
docker compose up -d --build
```

Wait ~60s, then verify:

```bash
docker compose ps
```

## Clean Up

```bash
# Stop containers
docker compose down

# Full cleanup: containers, images, volumes, networks
docker compose down --rmi all --volumes --remove-orphans

# Clean orphan Docker resources system-wide
docker image prune -af
docker volume prune -f
```

## URLs

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Management | http://localhost:15672 |

## Default Credentials

| Account | Username | Password |
|---------|----------|----------|
| Admin | `admin` | `admin123` |
| RabbitMQ | `guest` | `guest` |
