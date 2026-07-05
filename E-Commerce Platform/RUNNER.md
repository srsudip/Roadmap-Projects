# RUNNER.md — E-Commerce Platform

## Prerequisites

| Tool | Install |
|------|---------|
| **Colima** | `brew install colima` |
| **Docker CLI** | `brew install docker` |
| **Docker Compose** | `brew install docker-compose` |

## Start Colima

```bash
colima start --cpu 4 --memory 8 --disk 20
```

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

## Stop Colima

```bash
colima stop
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
