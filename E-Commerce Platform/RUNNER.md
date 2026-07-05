# RUNNER.md — E-Commerce Platform

## Prerequisites

| Tool | Version |
|------|---------|
| **Docker Desktop** | 24+ |

## Build & Start

```bash
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
